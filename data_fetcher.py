"""
Data Fetcher Module

Functions:
- fetch_player_data(): Fetches player stats using nfl-data-py
- fetch_defense_data(): Fetches team defense stats using nfl-data-py
- filter_active_players(df): Filters DataFrame for players active next season
"""

import pandas as pd

def fetch_player_data() -> pd.DataFrame:
    """Fetch player stats using nfl-data-py"""
    import nfl_data_py
    # Fetch regular season data for 2024
    df = nfl_data_py.import_seasonal_data([2024], s_type="REG")
    
    # Map player_id to player_name, last_name, and position using import_players
    info = nfl_data_py.import_players()
    
    # Check for joining key (gsis_id is standard in import_players, maps to player_id in seasonal data)
    join_key = "gsis_id" if "gsis_id" in info.columns else "player_id"
    
    if "player_id" in df.columns and join_key in info.columns:
        # Prepare info dataframe for merge
        # we want player_name (from display_name), last_name, position
        cols_to_extract = {
            join_key: "player_id",
            "display_name": "player_name",
            "last_name": "last_name",
            "position": "position"
        }
        
        # Select available columns and rename
        available_cols = [c for c in cols_to_extract.keys() if c in info.columns]
        info_subset = info[available_cols].rename(columns=cols_to_extract)
        
        # Merge
        # Note: we merge on "player_id" because we renamed join_key to "player_id" above
        if "player_id" in info_subset.columns:
            df = df.merge(info_subset, on="player_id", how="left")

    # Use columns from nfl-data-py docs for receiving stats
    columns = [
        "player_id", "player_name", "last_name", "position", "season", "season_type", 
        "targets", "receptions", 
        "attempts", "completions", "passing_yards", "passing_tds", # QB stats
        "rushing_yards", "rushing_tds", # RB stats
        "receiving_yards", "receiving_tds",
        "receiving_air_yards", "receiving_yards_after_catch", "receiving_first_downs", "target_share",
        "air_yards_share", "wopr_x", "fantasy_points_ppr", "games", "tgt_sh", "ay_sh", "yac_sh", "wopr_y",
        "ry_sh", "rtd_sh", "rfd_sh", "rtdfd_sh", "dom", "w8dom", "yptmpa", "ppr_sh"
    ]
    available = [col for col in columns if col in df.columns]
    df = df[available]
    return df

def fetch_defense_data() -> pd.DataFrame:
    """Fetch team defense stats using nfl-data-py by aggregating player stats"""
    import nfl_data_py
    
    # Fetch regular season data for 2024
    df = nfl_data_py.import_seasonal_data([2024], s_type="REG")
    
    # Fetch roster to map players to teams and positions
    roster = nfl_data_py.import_seasonal_rosters([2024])
    
    # Merge seasonal data with roster to get 'team' and 'position'
    # Use player_id as key
    if "player_id" in df.columns and "player_id" in roster.columns:
        # Keep only relevant roster columns to avoid dupes
        roster_subset = roster[["player_id", "team", "position"]]
        df = df.merge(roster_subset, on="player_id", how="inner")
        
    # Filter for defensive positions
    def_positions = ["DL", "DB", "LB", "CB", "S", "DE", "DT", "ILB", "OLB", "SAF", "SS", "FS"]
    # Note: Roster positions might be simplified (e.g. just DB, DL, LB) or detailed.
    # The check_positions.py output showed ['OL' 'QB' 'P' 'K' 'TE' 'LS' 'DL' 'DB' 'LB' 'RB' 'WR']
    # So ['DL', 'DB', 'LB'] covers the main groups.
    
    if "position" in df.columns:
        # Filter for defense
        # We also include 'P' and 'K' maybe? No, defense usually doesn't include K.
        # But Special Teams TDs might come from anywhere.
        # Let's stick to core defense.
        df_def = df[df["position"].isin(["DL", "DB", "LB"])].copy()
        
        # Aggregate by team
        agg_cols = ["sacks", "fantasy_points", "fantasy_points_ppr"]
        agg_dict = {col: "sum" for col in agg_cols if col in df_def.columns}
        
        if agg_dict:
            df_team = df_def.groupby("team").agg(agg_dict).reset_index()
        else:
            df_team = pd.DataFrame(columns=["team", "sacks", "fantasy_points", "fantasy_points_ppr"])
    else:
        # Fallback if position missing
        df_team = pd.DataFrame(columns=["team", "sacks", "fantasy_points", "fantasy_points_ppr"])

    # Map team code to team name using import_team_desc
    team_info = nfl_data_py.import_team_desc()
    if "team" in df_team.columns and "team_abbr" in team_info.columns:
         # team_info uses 'team_abbr' for the code (e.g. SEA, NYJ)
        df_team = df_team.merge(team_info[["team_abbr", "team_name"]], left_on="team", right_on="team_abbr", how="left")
        
    # Ensure columns exist
    for col in ["sacks", "fantasy_points", "fantasy_points_ppr"]:
        if col not in df_team.columns:
            df_team[col] = 0

    return df_team

def filter_active_players(df: pd.DataFrame) -> pd.DataFrame:
    """Filter DataFrame for players active next season"""
    # Filter for players with targets, receptions, or passing attempts (offensive contributors)
    filter_cols = [col for col in ["targets", "receptions", "attempts"] if col in df.columns]
    if filter_cols:
        df = df[df[filter_cols].sum(axis=1) > 0]
    
    # Remove players with missing key stats
    drop_cols = [col for col in ["player_id", "ppr_sh"] if col in df.columns]
    df = df.dropna(subset=drop_cols)
    return df

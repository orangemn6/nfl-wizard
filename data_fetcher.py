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
    # Map player_id to player_name using import_players
    info = nfl_data_py.import_players()
    if "player_id" in df.columns and "player_id" in info.columns and "player_name" in info.columns:
        df = df.merge(info[["player_id", "player_name"]], on="player_id", how="left")
    # Use columns from nfl-data-py docs for receiving stats
    columns = [
        "player_id", "player_name", "season", "season_type", "targets", "receptions", "receiving_yards", "receiving_tds",
        "receiving_air_yards", "receiving_yards_after_catch", "receiving_first_downs", "target_share",
        "air_yards_share", "wopr_x", "fantasy_points_ppr", "games", "tgt_sh", "ay_sh", "yac_sh", "wopr_y",
        "ry_sh", "rtd_sh", "rfd_sh", "rtdfd_sh", "dom", "w8dom", "yptmpa", "ppr_sh"
    ]
    available = [col for col in columns if col in df.columns]
    df = df[available]
    return df

def fetch_defense_data() -> pd.DataFrame:
    """Fetch team defense stats using nfl-data-py"""
    import nfl_data_py
    # Fetch regular season data for 2024
    df = nfl_data_py.import_seasonal_data([2024], s_type="REG")
    # Map team code to team name using import_team_desc
    team_info = nfl_data_py.import_team_desc()
    if "team" in df.columns and "team" in team_info.columns and "team_name" in team_info.columns:
        df = df.merge(team_info[["team", "team_name"]], on="team", how="left")
    # Use columns from nfl-data-py docs for defense stats
    columns = [
        "team", "team_name", "season", "season_type", "sacks", "fantasy_points", "fantasy_points_ppr"
    ]
    available = [col for col in columns if col in df.columns]
    df = df[available]
    return df

def filter_active_players(df: pd.DataFrame) -> pd.DataFrame:
    """Filter DataFrame for players active next season"""
    # Filter for players with targets or receptions (offensive contributors)
    filter_cols = [col for col in ["targets", "receptions"] if col in df.columns]
    if filter_cols:
        df = df[df[filter_cols].sum(axis=1) > 0]
    # Remove players with missing key stats
    drop_cols = [col for col in ["player_id", "ppr_sh"] if col in df.columns]
    df = df.dropna(subset=drop_cols)
    return df
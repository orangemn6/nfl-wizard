"""
NFL Ranking App: Weighted stats, player/defense ranking, PDF export.
"""

import nfl_data_py
from data_fetcher import fetch_player_data, fetch_defense_data, filter_active_players
from stats_analyzer import calculate_weights
from ranker import rank_players, rank_defenses
from pdf_exporter import export_pdf

def main():
    # Fetch and filter player data
    df = fetch_player_data()
    df = filter_active_players(df)

    # Map last names using player_id and gsis_id
    players_info = nfl_data_py.import_players()
    if "player_id" in df.columns and "gsis_id" in players_info.columns and "last_name" in players_info.columns:
        last_name_map = dict(zip(players_info["gsis_id"], players_info["last_name"]))
        df["last_name"] = df["player_id"].map(last_name_map)
    else:
        df["last_name"] = df["player_id"]

    # Calculate stat weights
    weights = calculate_weights(df)

    # Rank players and defenses
    ranked_players = rank_players(df, weights)
    defense_df = fetch_defense_data()
    ranked_defenses = rank_defenses(defense_df)

    # Export to PDF
    export_pdf(ranked_players, ranked_defenses)

if __name__ == "__main__":
    main()
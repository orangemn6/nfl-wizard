"""
NFL Ranking App: Weighted stats, player/defense ranking, PDF export.
"""

import nfl_data_py
from data_fetcher import fetch_player_data, fetch_defense_data, filter_active_players
from stats_analyzer import calculate_weights
from ranker import rank_players, rank_defenses
from pdf_exporter import export_pdf

def main():
    print("Fetching player data...")
    # Fetch and filter player data
    df = fetch_player_data()
    print(f"Fetched {len(df)} player records.")
    
    df = filter_active_players(df)
    print(f"Filtered to {len(df)} active players.")

    # Calculate stat weights
    print("Calculating weights...")
    weights = calculate_weights(df)
    # print("Weights:", weights)

    # Rank players and defenses
    print("Ranking players...")
    ranked_players = rank_players(df, weights)
    
    print("Fetching and ranking defenses...")
    defense_df = fetch_defense_data()
    ranked_defenses = rank_defenses(defense_df)

    # Export to PDF
    print("Exporting to PDF...")
    export_pdf(ranked_players, ranked_defenses)
    print("Done! Check nfl_rankings.pdf")

if __name__ == "__main__":
    main()

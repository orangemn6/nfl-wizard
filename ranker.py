"""
Ranker Module

Functions:
- rank_players(df, weights): Ranks players using weighted stats
- rank_defenses(df): Ranks team defenses
"""

import pandas as pd

def rank_players(df: pd.DataFrame, weights: dict) -> pd.DataFrame:
    """
    Rank players using weighted stats.
    Returns a DataFrame sorted by overall score.
    """
    stat_cols = list(weights.keys())
    # Normalize each stat column if present
    for col in stat_cols:
        if col in df.columns:
            # Avoid division by zero
            std = df[col].std()
            if std == 0:
                std = 1
            df[col + "_norm"] = (df[col] - df[col].mean()) / std
            
    # Calculate weighted score
    df["score"] = sum(df[col + "_norm"] * weights[col] for col in stat_cols if col + "_norm" in df.columns)
    # Sort by score
    ranked = df.sort_values("score", ascending=False)
    return ranked

def rank_defenses(df: pd.DataFrame) -> pd.DataFrame:
    """
    Rank team defenses.
    Returns a DataFrame sorted by defensive score.
    """
    # Use available columns for defense ranking
    stat_cols = [col for col in ["sacks", "fantasy_points", "fantasy_points_ppr"] if col in df.columns]
    for col in stat_cols:
        std = df[col].std()
        if std == 0:
            std = 1
        df[col + "_norm"] = (df[col] - df[col].mean()) / std
        
    # Defensive score: sum normalized stats
    df["def_score"] = df[[col + "_norm" for col in stat_cols]].sum(axis=1)
    return df.sort_values("def_score", ascending=False)

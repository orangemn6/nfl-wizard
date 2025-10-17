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
            df[col + "_norm"] = (df[col] - df[col].mean()) / (df[col].std() if df[col].std() else 1)
    # Calculate weighted score
    df["score"] = sum(df[col + "_norm"] * weights[col] for col in stat_cols if col + "_norm" in df.columns)
    # Sort and select top 45 by score
    ranked = df.sort_values("score", ascending=False).head(45)
    return ranked

def rank_defenses(df: pd.DataFrame) -> pd.DataFrame:
    """
    Rank team defenses.
    Returns a DataFrame sorted by defensive score.
    """
    # Use available columns for defense ranking
    stat_cols = [col for col in ["sacks", "fantasy_points", "fantasy_points_ppr"] if col in df.columns]
    for col in stat_cols:
        df[col + "_norm"] = (df[col] - df[col].mean()) / (df[col].std() if df[col].std() else 1)
    # Defensive score: sum normalized stats
    df["def_score"] = df[[col + "_norm" for col in stat_cols]].sum(axis=1)
    return df.sort_values("def_score", ascending=False)
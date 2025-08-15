"""
Stats Analyzer Module

Functions:
- calculate_weights(df): Calculates correlation-based weights for each stat column
"""

import pandas as pd

def calculate_weights(df: pd.DataFrame) -> dict:
    """
    Calculate correlation-based weights for each stat column.
    Returns a dictionary mapping stat column names to weights.
    """
    stat_cols = [
        "tgt_sh", "ay_sh", "yac_sh", "wopr_x", "wopr_y", "ry_sh", "rtd_sh", "rfd_sh",
        "rtdfd_sh", "dom", "w8dom", "yptmpa"
    ]
    correlations = {}
    for col in stat_cols:
        if col in df.columns and "ppr_sh" in df.columns:
            corr = df[col].corr(df["ppr_sh"])
            correlations[col] = abs(corr) if corr is not None else 0
    total = sum(correlations.values())
    weights = {col: (correlations[col] / total if total > 0 else 0) for col in correlations}
    return weights
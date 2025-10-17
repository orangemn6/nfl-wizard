"""
PDF Exporter Module

Exports rankings to a multi-page PDF, with each offensive position (QB, RB, WR, TE) and defenses on their own page, showing last names and scores for top 15 per group.
"""

import pandas as pd

def export_pdf(ranked_players: pd.DataFrame, ranked_defenses: pd.DataFrame):
    from reportlab.lib.pagesizes import letter
    from reportlab.pdfgen import canvas
    from reportlab.lib import colors

    c = canvas.Canvas("nfl_rankings.pdf", pagesize=letter)
    width, height = letter

    def draw_last_names(df, title, score_col="score"):
        c.showPage()  # Ensure each call starts a new page
        c.setFont("Helvetica-Bold", 18)
        c.setFillColor(colors.darkblue)
        c.drawString(40, height - 40, title)
        c.setFont("Helvetica", 12)
        c.setFillColor(colors.black)
        y = height - 70

        for _, row in df.iterrows():
            last = row["last_name"] if "last_name" in row else str(row.get("player_id", ""))
            score = row[score_col] if score_col in row else ""
            line = f"{last} - {score}"
            c.drawString(40, y, line)
            y -= 18
            if y < 60:
                c.showPage()
                c.setFont("Helvetica-Bold", 18)
                c.setFillColor(colors.darkblue)
                c.drawString(40, height - 40, title)
                c.setFont("Helvetica", 12)
                c.setFillColor(colors.black)
                y = height - 70

    # Top 15 for each offensive position, each on its own page
    if "position" in ranked_players.columns:
        for pos in ["QB", "RB", "WR", "TE"]:
            pos_df = ranked_players[ranked_players["position"] == pos].head(15)
            draw_last_names(pos_df, f"Top 15 {pos}s", score_col="score")
    else:
        draw_last_names(ranked_players.head(15), "Top 15 Offensive Players", score_col="score")

    # Top 15 defenses on their own page
    draw_last_names(ranked_defenses.head(15), "Top 15 Team Defenses", score_col="def_score")

    c.save()
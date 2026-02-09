"""
PDF Exporter Module

Exports rankings to a multi-page PDF, with each offensive position (QB, RB, WR, TE) and defenses on their own page, showing last names and scores for top 15 per group.
"""

import pandas as pd
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from reportlab.lib import colors

def export_pdf(ranked_players: pd.DataFrame, ranked_defenses: pd.DataFrame):
    c = canvas.Canvas("nfl_rankings.pdf", pagesize=letter)
    width, height = letter
    
    # Layout constants
    MARGIN_LEFT = 40
    ROW_HEIGHT = 20
    HEADER_Y_OFFSET = 50
    START_Y = height - 80
    
    def draw_documentation_page():
        """Draws the first page explaining the methodology."""
        c.setFont("Helvetica-Bold", 24)
        c.setFillColor(colors.darkblue)
        c.drawString(MARGIN_LEFT, height - 50, "NFL Wizard: Ranking Methodology")
        
        c.setFont("Helvetica", 12)
        c.setFillColor(colors.black)
        y = height - 100
        line_height = 14
        
        # Introduction
        lines = [
            "This report ranks NFL players and defenses based on advanced statistical models.",
            "The goal is to identify high-upside performers using historical data correlations.",
            "",
            "1. Player Scoring Algorithm",
            "---------------------------",
            "Player rankings are determined by a 'Weighted Efficiency Score'.",
            "",
            "Step A: Weight Calculation",
            "We calculate the weight of various advanced statistics based on their correlation",
            "with a player's PPR (Points Per Reception) Share. Stats with higher correlation",
            "receive higher weights. The weights are normalized to sum to 1.",
            "",
            "Key Statistics Used:",
            "  - Target Share (tgt_sh)",
            "  - Air Yards Share (ay_sh)",
            "  - Weighted Opportunity Rating (wopr_x, wopr_y)",
            "  - Yards After Catch Share (yac_sh)",
            "  - Dominator Rating (dom, w8dom)",
            "  - Yards Per Team Pass Attempt (yptmpa)",
            "",
            "Step B: Normalization (Z-Score)",
            "To compare different stats on the same scale, we normalize each stat:",
            "  Z = (Value - Average) / Standard Deviation",
            "This tells us how many standard deviations a player is above or below the league average.",
            "",
            "Step C: Final Score",
            "  Score = Sum(Normalized_Stat * Weight)",
            "",
            "2. Defense Scoring Algorithm",
            "----------------------------",
            "Defenses are ranked by aggregating individual player stats (DL, LB, DB) per team.",
            "The score is a simple sum of normalized values for:",
            "  - Total Sacks",
            "  - Total Fantasy Points",
            "  - PPR Fantasy Points",
            "",
            "3. Visual Key",
            "------------",
            "Players highlighted in RED have a PPR Share > 15%, indicating they are a",
            "focal point of their team's offense."
        ]
        
        for line in lines:
            c.drawString(MARGIN_LEFT, y, line)
            y -= line_height
            
        c.showPage()

    def draw_header(title, is_defense=False):
        c.setFont("Helvetica-Bold", 18)
        c.setFillColor(colors.darkblue)
        c.drawString(MARGIN_LEFT, height - 40, title)
        
        # Draw column headers
        c.setFont("Helvetica-Bold", 10)
        c.setFillColor(colors.black)
        
        headers_y = height - 60
        c.drawString(MARGIN_LEFT, headers_y, "Rank")
        c.drawString(MARGIN_LEFT + 40, headers_y, "Name")
        c.drawString(MARGIN_LEFT + 200, headers_y, "Score")
        c.drawString(MARGIN_LEFT + 260, headers_y, "FPts (PPR)")
        
        if is_defense:
            c.drawString(MARGIN_LEFT + 340, headers_y, "Sacks")
        else:
            c.drawString(MARGIN_LEFT + 340, headers_y, "Games")
            c.drawString(MARGIN_LEFT + 400, headers_y, "Key Stats (Yds/TDs)")
            
        c.line(MARGIN_LEFT, headers_y - 5, width - MARGIN_LEFT, headers_y - 5)

    def draw_section(df, title, is_defense=False):
        draw_header(title, is_defense)
        c.setFont("Helvetica", 10)
        c.setFillColor(colors.black)
        y = START_Y

        score_col = "def_score" if is_defense else "score"

        # Re-implementing loop with enumerate for ranking
        for rank, (_, row) in enumerate(df.iterrows(), 1):
            
            # Name Logic
            if "last_name" in row and pd.notna(row["last_name"]):
                name = f"{row['player_name']} ({row['last_name']})"
                # Simplify to just Full Name if available, or Last Name
                name = row.get("player_name", row["last_name"])
            elif "team_name" in row and pd.notna(row["team_name"]):
                name = row["team_name"]
            else:
                name = str(row.get("player_id", row.get("team", "Unknown")))
            
            # Truncate long names
            if len(name) > 25:
                name = name[:23] + "..."

            # Score
            score = f"{row[score_col]:.2f}" if score_col in row else "-"
            
            # Fantasy Points
            fpts = f"{row['fantasy_points_ppr']:.1f}" if "fantasy_points_ppr" in row and pd.notna(row['fantasy_points_ppr']) else "-"
            
            # Stats Logic
            stats_str = ""
            if is_defense:
                sacks = f"{row['sacks']:.0f}" if "sacks" in row else "-"
                stats_str = sacks # Column 340 is Sacks
            else:
                games_val = f"{row['games']:.0f}" if "games" in row and pd.notna(row['games']) else "-"
                
                # Key Stats based on position
                pos = row.get("position", "")
                if pos == "QB":
                    yds = row.get("passing_yards", 0)
                    tds = row.get("passing_tds", 0)
                    stats_str = f"{yds:.0f} Pass Yds / {tds:.0f} TDs"
                elif pos == "RB":
                    yds = row.get("rushing_yards", 0)
                    tds = row.get("rushing_tds", 0)
                    rec = row.get("receptions", 0)
                    stats_str = f"{yds:.0f} Rush Yds / {tds:.0f} TDs / {rec:.0f} Rec"
                elif pos in ["WR", "TE"]:
                    yds = row.get("receiving_yards", 0)
                    tds = row.get("receiving_tds", 0)
                    rec = row.get("receptions", 0)
                    stats_str = f"{yds:.0f} Rec Yds / {tds:.0f} TDs / {rec:.0f} Rec"
                else:
                    stats_str = "-"
            
            # Highlight Logic (High PPR Share)
            is_highlight = False
            if not is_defense and "ppr_sh" in row and pd.notna(row["ppr_sh"]) and row["ppr_sh"] > 0.15:
                is_highlight = True
            
            if is_highlight:
                c.setFillColor(colors.red)
            else:
                c.setFillColor(colors.black)

            # Draw Row
            c.drawString(MARGIN_LEFT, y, str(rank))
            c.drawString(MARGIN_LEFT + 40, y, name)
            c.drawString(MARGIN_LEFT + 200, y, score)
            c.drawString(MARGIN_LEFT + 260, y, fpts)
            
            if is_defense:
                 c.drawString(MARGIN_LEFT + 340, y, stats_str)
            else:
                 c.drawString(MARGIN_LEFT + 340, y, games_val)
                 c.drawString(MARGIN_LEFT + 400, y, stats_str)

            y -= ROW_HEIGHT
            
            if y < 60:
                c.showPage()
                draw_header(title + " (Cont.)", is_defense)
                c.setFont("Helvetica", 10)
                y = START_Y
        
        c.showPage()

    # --- Draw Documentation Page ---
    draw_documentation_page()

    # Top 15 for each offensive position
    if "position" in ranked_players.columns:
        for pos in ["QB", "RB", "WR", "TE"]:
            pos_df = ranked_players[ranked_players["position"] == pos].head(15)
            if not pos_df.empty:
                draw_section(pos_df, f"Top 15 {pos}s", is_defense=False)
    else:
        draw_section(ranked_players.head(15), "Top 15 Offensive Players", is_defense=False)

    # Top 15 defenses
    if not ranked_defenses.empty:
        draw_section(ranked_defenses.head(15), "Top 15 Team Defenses", is_defense=True)

    c.save()

# NFL Player & Defense Ranking App

## Overview
This app ranks NFL players and team defenses for the upcoming season using a weighted combination of advanced stats. Rankings are exported to a multi-page PDF, with top PPR share players highlighted.

## Features
- Fetches player and defense stats for the next NFL season using `nfl-data-py`
- Filters for active players only
- Calculates stat weights based on historical correlation with fantasy points
- Ranks top 45 players per offensive position (QB, RB, WR, TE)
- Ranks team defenses
- Exports results to PDF (each position/defense on a separate page)
- Highlights top performers in PPR share

## Usage

1. **Install dependencies:**
   ```
   pip install -r requirements.txt
   ```

2. **Run the app:**
   ```
   python main.py
   ```

3. **Output:**
   - `nfl_rankings.pdf` will be generated in the project directory.

## Algorithm Details

- **Stat Columns Used:** tgt_sh, ay_sh, yac_sh, wopr, ry_sh, rtd_sh, rfd_sh, rtdfd_sh, dom, w8dom, yptmpa, ppr_sh
- **Weight Calculation:** Pearson correlation between each stat and PPR share, normalized to sum to 1
- **Ranking:** Weighted sum of normalized stats for each player; team defenses ranked by normalized defensive stats

## File Structure

- `main.py`: Orchestrates workflow
- `data_fetcher.py`: Fetches and filters data
- `stats_analyzer.py`: Calculates stat weights
- `ranker.py`: Ranks players and defenses
- `pdf_exporter.py`: Generates PDF report
- `requirements.txt`: Dependencies

## Dependencies

- pandas
- reportlab
- nfl-data-py

## Notes

- Only players active for the next season are included in rankings.
- Top PPR share players are visually highlighted in the PDF.
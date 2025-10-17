# NFL Player & Defense Ranking App

## Overview
This app ranks NFL players and team defenses for the upcoming season using a weighted combination of advanced stats. Rankings are exported to a multi-page PDF, with top PPR share players highlighted.

**Now available in Java!** The application has been fully refactored to Java with Maven for improved performance and enterprise-ready architecture.

## Features
- Fetches player and defense stats for the next NFL season
- Filters for active players only
- Calculates stat weights based on historical correlation with fantasy points
- Ranks top 45 players per offensive position (QB, RB, WR, TE)
- Ranks team defenses
- Exports results to PDF (each position/defense on a separate page)
- Highlights top performers in PPR share

## Java Version (Recommended)

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Usage

1. **Build the application:**
   ```bash
   mvn clean package
   ```

2. **Run the app:**
   ```bash
   java -jar target/nfl-wizard-1.0.0.jar
   ```
   
   Or use Maven directly:
   ```bash
   mvn exec:java
   ```

3. **Output:**
   - `nfl_rankings.pdf` will be generated in the project directory.

## Python Version (Legacy)

**Note:** Python files have been moved to the `archive/` folder.

### Usage

1. **Install dependencies:**
   ```bash
   pip install -r archive/requirements.txt
   ```

2. **Run the app:**
   ```bash
   python archive/main.py
   ```

3. **Output:**
   - `nfl_rankings.pdf` will be generated in the project directory.

## Algorithm Details

- **Stat Columns Used:** tgt_sh, ay_sh, yac_sh, wopr, ry_sh, rtd_sh, rfd_sh, rtdfd_sh, dom, w8dom, yptmpa, ppr_sh
- **Weight Calculation:** Pearson correlation between each stat and PPR share, normalized to sum to 1
- **Ranking:** Weighted sum of normalized stats for each player; team defenses ranked by normalized defensive stats

## Architecture

### Java Implementation (src/main/java/com/nflwizard/)

- **Main.java**: Orchestrates workflow
- **model/Player.java**: Player data model
- **model/Defense.java**: Defense data model
- **service/DataFetcher.java**: Fetches and filters NFL data
- **service/StatsAnalyzer.java**: Calculates stat weights using Pearson correlation
- **service/Ranker.java**: Ranks players and defenses
- **service/PDFExporter.java**: Generates PDF report using iText

### Python Implementation (Legacy - in archive/ folder)

- `archive/main.py`: Orchestrates workflow
- `archive/data_fetcher.py`: Fetches and filters data
- `archive/stats_analyzer.py`: Calculates stat weights
- `archive/ranker.py`: Ranks players and defenses
- `archive/pdf_exporter.py`: Generates PDF report
- `archive/requirements.txt`: Python dependencies

## Dependencies

### Java
- Apache Commons Math (statistical calculations)
- iText (PDF generation)
- Jackson (JSON processing)
- Apache HttpClient (API calls)
- SLF4J (logging)

### Python (Legacy)
- pandas
- reportlab
- nfl-data-py

## Notes

- Only players active for the next season are included in rankings.
- Top PPR share players are visually highlighted in the PDF.
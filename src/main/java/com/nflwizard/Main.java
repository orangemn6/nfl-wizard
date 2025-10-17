package com.nflwizard;

import com.nflwizard.model.Defense;
import com.nflwizard.model.Player;
import com.nflwizard.service.DataFetcher;
import com.nflwizard.service.PDFExporter;
import com.nflwizard.service.Ranker;
import com.nflwizard.service.StatsAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * NFL Player & Defense Ranking Application
 * 
 * Ranks NFL players and team defenses for the upcoming season using 
 * a weighted combination of advanced stats. Rankings are exported to 
 * a multi-page PDF, with top PPR share players highlighted.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int SEASON = 2024;
    private static final String OUTPUT_FILE = "nfl_rankings.pdf";
    
    public static void main(String[] args) {
        logger.info("Starting NFL Wizard - Player & Defense Ranking Application");
        logger.info("Season: {}", SEASON);
        
        try {
            // Initialize services
            DataFetcher dataFetcher = new DataFetcher();
            StatsAnalyzer statsAnalyzer = new StatsAnalyzer();
            Ranker ranker = new Ranker();
            PDFExporter pdfExporter = new PDFExporter();
            
            // Step 1: Fetch player data
            logger.info("Step 1: Fetching player data...");
            List<Player> players = dataFetcher.fetchPlayerData(SEASON);
            
            // Step 2: Filter active players
            logger.info("Step 2: Filtering active players...");
            List<Player> activePlayers = dataFetcher.filterActivePlayers(players);
            
            // Step 3: Calculate stat weights
            logger.info("Step 3: Calculating stat weights...");
            Map<String, Double> weights = statsAnalyzer.calculateWeights(activePlayers);
            
            // Step 4: Rank players by position
            logger.info("Step 4: Ranking players by position...");
            Map<String, List<Player>> rankedByPosition = ranker.rankPlayersByPosition(activePlayers, weights, 15);
            
            // Step 5: Fetch and rank defenses
            logger.info("Step 5: Fetching and ranking defenses...");
            List<Defense> defenses = dataFetcher.fetchDefenseData(SEASON);
            List<Defense> rankedDefenses = ranker.rankDefenses(defenses);
            
            // Step 6: Export to PDF
            logger.info("Step 6: Exporting results to PDF...");
            pdfExporter.exportToPDF(rankedByPosition, rankedDefenses, OUTPUT_FILE);
            
            // Print summary
            logger.info("===========================================");
            logger.info("Rankings Summary:");
            for (String position : new String[]{"QB", "RB", "WR", "TE"}) {
                List<Player> positionPlayers = rankedByPosition.get(position);
                if (positionPlayers != null && !positionPlayers.isEmpty()) {
                    logger.info("  Top {} {}: {}", 
                        Math.min(3, positionPlayers.size()), 
                        position, 
                        getTopPlayerNames(positionPlayers, 3)
                    );
                }
            }
            logger.info("  Top 3 Defenses: {}", getTopDefenseNames(rankedDefenses, 3));
            logger.info("===========================================");
            logger.info("Results exported to: {}", OUTPUT_FILE);
            logger.info("NFL Wizard completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error running NFL Wizard", e);
            System.exit(1);
        }
    }
    
    /**
     * Get top player names for summary
     */
    private static String getTopPlayerNames(List<Player> players, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(count, players.size()); i++) {
            if (i > 0) sb.append(", ");
            sb.append(players.get(i).getLastName());
        }
        return sb.toString();
    }
    
    /**
     * Get top defense names for summary
     */
    private static String getTopDefenseNames(List<Defense> defenses, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(count, defenses.size()); i++) {
            if (i > 0) sb.append(", ");
            sb.append(defenses.get(i).getTeamName());
        }
        return sb.toString();
    }
}

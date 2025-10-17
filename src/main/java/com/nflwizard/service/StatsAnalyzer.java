package com.nflwizard.service;

import com.nflwizard.model.Player;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyzes player statistics and calculates correlation-based weights
 */
public class StatsAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(StatsAnalyzer.class);
    
    private static final String[] STAT_COLUMNS = {
        "tgt_sh", "ay_sh", "yac_sh", "wopr_y", "ry_sh", "rtd_sh", 
        "rfd_sh", "rtdfd_sh", "dom", "w8dom", "yptmpa"
    };
    
    /**
     * Calculate correlation-based weights for each stat column
     * Returns a map of stat names to their normalized weights
     */
    public Map<String, Double> calculateWeights(List<Player> players) {
        logger.info("Calculating stat weights for {} players", players.size());
        
        Map<String, Double> correlations = new HashMap<>();
        PearsonsCorrelation correlation = new PearsonsCorrelation();
        
        // Get ppr_sh values for all players
        double[] pprShValues = new double[players.size()];
        for (int i = 0; i < players.size(); i++) {
            pprShValues[i] = players.get(i).getPprSh();
        }
        
        // Calculate correlation for each stat
        for (String statName : STAT_COLUMNS) {
            double[] statValues = new double[players.size()];
            
            for (int i = 0; i < players.size(); i++) {
                Double value = players.get(i).getStatValue(statName);
                statValues[i] = (value != null) ? value : 0.0;
            }
            
            try {
                double corr = correlation.correlation(statValues, pprShValues);
                // Use absolute value of correlation
                correlations.put(statName, Math.abs(corr));
            } catch (Exception e) {
                logger.warn("Could not calculate correlation for {}: {}", statName, e.getMessage());
                correlations.put(statName, 0.0);
            }
        }
        
        // Normalize weights to sum to 1
        double total = correlations.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        Map<String, Double> weights = new HashMap<>();
        if (total > 0) {
            for (Map.Entry<String, Double> entry : correlations.entrySet()) {
                weights.put(entry.getKey(), entry.getValue() / total);
            }
        } else {
            // If no valid correlations, use equal weights
            double equalWeight = 1.0 / STAT_COLUMNS.length;
            for (String statName : STAT_COLUMNS) {
                weights.put(statName, equalWeight);
            }
        }
        
        logger.info("Calculated weights: {}", weights);
        return weights;
    }
}

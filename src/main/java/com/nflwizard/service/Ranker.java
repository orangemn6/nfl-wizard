package com.nflwizard.service;

import com.nflwizard.model.Defense;
import com.nflwizard.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Ranks players and defenses based on their stats
 */
public class Ranker {
    private static final Logger logger = LoggerFactory.getLogger(Ranker.class);
    
    private static final String[] STAT_COLUMNS = {
        "tgt_sh", "ay_sh", "yac_sh", "wopr_y", "ry_sh", "rtd_sh", 
        "rfd_sh", "rtdfd_sh", "dom", "w8dom", "yptmpa"
    };
    
    private static final String[] DEFENSE_STATS = {
        "sacks", "fantasy_points", "fantasy_points_ppr"
    };
    
    /**
     * Rank players using weighted stats
     * Returns the top 45 players sorted by score
     */
    public List<Player> rankPlayers(List<Player> players, Map<String, Double> weights) {
        logger.info("Ranking {} players", players.size());
        
        // Calculate mean and standard deviation for each stat
        Map<String, Double> means = new HashMap<>();
        Map<String, Double> stdDevs = new HashMap<>();
        
        for (String statName : STAT_COLUMNS) {
            List<Double> values = new ArrayList<>();
            for (Player player : players) {
                Double value = player.getStatValue(statName);
                if (value != null) {
                    values.add(value);
                }
            }
            
            if (!values.isEmpty()) {
                double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = values.stream()
                    .mapToDouble(v -> Math.pow(v - mean, 2))
                    .average()
                    .orElse(0.0);
                double stdDev = Math.sqrt(variance);
                
                means.put(statName, mean);
                stdDevs.put(statName, stdDev > 0 ? stdDev : 1.0);
            } else {
                means.put(statName, 0.0);
                stdDevs.put(statName, 1.0);
            }
        }
        
        // Normalize stats and calculate weighted scores
        for (Player player : players) {
            double score = 0.0;
            
            for (String statName : STAT_COLUMNS) {
                Double value = player.getStatValue(statName);
                if (value != null && weights.containsKey(statName)) {
                    double mean = means.get(statName);
                    double stdDev = stdDevs.get(statName);
                    double normalizedValue = (value - mean) / stdDev;
                    
                    player.setNormalizedStat(statName, normalizedValue);
                    score += normalizedValue * weights.get(statName);
                }
            }
            
            player.setScore(score);
        }
        
        // Sort by score and take top 45
        List<Player> ranked = players.stream()
            .sorted((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()))
            .limit(45)
            .collect(Collectors.toList());
        
        logger.info("Ranked top {} players", ranked.size());
        return ranked;
    }
    
    /**
     * Rank players by position
     * Returns top players for each position (QB, RB, WR, TE)
     */
    public Map<String, List<Player>> rankPlayersByPosition(List<Player> players, Map<String, Double> weights, int topN) {
        logger.info("Ranking players by position (top {})", topN);
        
        // First, rank all players
        List<Player> rankedPlayers = rankPlayers(players, weights);
        
        // Group by position
        Map<String, List<Player>> byPosition = new HashMap<>();
        String[] positions = {"QB", "RB", "WR", "TE"};
        
        for (String position : positions) {
            List<Player> positionPlayers = rankedPlayers.stream()
                .filter(p -> position.equals(p.getPosition()))
                .limit(topN)
                .collect(Collectors.toList());
            byPosition.put(position, positionPlayers);
            logger.info("Position {}: {} players", position, positionPlayers.size());
        }
        
        return byPosition;
    }
    
    /**
     * Rank team defenses based on their stats
     */
    public List<Defense> rankDefenses(List<Defense> defenses) {
        logger.info("Ranking {} defenses", defenses.size());
        
        // Calculate mean and standard deviation for each defense stat
        Map<String, Double> means = new HashMap<>();
        Map<String, Double> stdDevs = new HashMap<>();
        
        for (String statName : DEFENSE_STATS) {
            List<Double> values = new ArrayList<>();
            for (Defense defense : defenses) {
                Double value = defense.getStatValue(statName);
                if (value != null) {
                    values.add(value);
                }
            }
            
            if (!values.isEmpty()) {
                double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = values.stream()
                    .mapToDouble(v -> Math.pow(v - mean, 2))
                    .average()
                    .orElse(0.0);
                double stdDev = Math.sqrt(variance);
                
                means.put(statName, mean);
                stdDevs.put(statName, stdDev > 0 ? stdDev : 1.0);
            } else {
                means.put(statName, 0.0);
                stdDevs.put(statName, 1.0);
            }
        }
        
        // Normalize stats and calculate scores
        for (Defense defense : defenses) {
            double score = 0.0;
            
            for (String statName : DEFENSE_STATS) {
                Double value = defense.getStatValue(statName);
                if (value != null) {
                    double mean = means.get(statName);
                    double stdDev = stdDevs.get(statName);
                    double normalizedValue = (value - mean) / stdDev;
                    
                    defense.setNormalizedStat(statName, normalizedValue);
                    score += normalizedValue;
                }
            }
            
            defense.setDefScore(score);
        }
        
        // Sort by defensive score
        List<Defense> ranked = defenses.stream()
            .sorted((d1, d2) -> Double.compare(d2.getDefScore(), d1.getDefScore()))
            .collect(Collectors.toList());
        
        logger.info("Ranked {} defenses", ranked.size());
        return ranked;
    }
}

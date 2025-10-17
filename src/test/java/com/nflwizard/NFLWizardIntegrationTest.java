package com.nflwizard;

import com.nflwizard.model.Defense;
import com.nflwizard.model.Player;
import com.nflwizard.service.DataFetcher;
import com.nflwizard.service.Ranker;
import com.nflwizard.service.StatsAnalyzer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the NFL Wizard application
 */
public class NFLWizardIntegrationTest {
    
    @Test
    public void testDataFetcherGeneratesPlayers() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Player> players = dataFetcher.fetchPlayerData(2024);
        
        assertNotNull(players, "Players list should not be null");
        assertFalse(players.isEmpty(), "Players list should not be empty");
        assertTrue(players.size() >= 50, "Should generate at least 50 players");
        
        // Verify we have players for each position
        assertTrue(players.stream().anyMatch(p -> "QB".equals(p.getPosition())), "Should have QB players");
        assertTrue(players.stream().anyMatch(p -> "RB".equals(p.getPosition())), "Should have RB players");
        assertTrue(players.stream().anyMatch(p -> "WR".equals(p.getPosition())), "Should have WR players");
        assertTrue(players.stream().anyMatch(p -> "TE".equals(p.getPosition())), "Should have TE players");
    }
    
    @Test
    public void testDataFetcherGeneratesDefenses() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Defense> defenses = dataFetcher.fetchDefenseData(2024);
        
        assertNotNull(defenses, "Defenses list should not be null");
        assertEquals(32, defenses.size(), "Should generate 32 team defenses");
        
        // Verify defense has required fields
        Defense firstDefense = defenses.get(0);
        assertNotNull(firstDefense.getTeam(), "Defense team code should not be null");
        assertNotNull(firstDefense.getTeamName(), "Defense team name should not be null");
        assertTrue(firstDefense.getSacks() > 0, "Defense should have sacks");
    }
    
    @Test
    public void testFilterActivePlayersWorks() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Player> players = dataFetcher.fetchPlayerData(2024);
        List<Player> activePlayers = dataFetcher.filterActivePlayers(players);
        
        assertNotNull(activePlayers, "Active players list should not be null");
        assertFalse(activePlayers.isEmpty(), "Active players list should not be empty");
        
        // All active players should have targets and receptions
        for (Player player : activePlayers) {
            assertTrue(player.getTargets() > 0, "Active player should have targets");
            assertTrue(player.getReceptions() > 0, "Active player should have receptions");
        }
    }
    
    @Test
    public void testStatsAnalyzerCalculatesWeights() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Player> players = dataFetcher.fetchPlayerData(2024);
        List<Player> activePlayers = dataFetcher.filterActivePlayers(players);
        
        StatsAnalyzer statsAnalyzer = new StatsAnalyzer();
        Map<String, Double> weights = statsAnalyzer.calculateWeights(activePlayers);
        
        assertNotNull(weights, "Weights map should not be null");
        assertFalse(weights.isEmpty(), "Weights map should not be empty");
        
        // Verify weights sum to approximately 1.0
        double sum = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(1.0, sum, 0.01, "Weights should sum to approximately 1.0");
        
        // All weights should be non-negative
        for (Double weight : weights.values()) {
            assertTrue(weight >= 0, "All weights should be non-negative");
        }
    }
    
    @Test
    public void testRankerRanksPlayersByPosition() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Player> players = dataFetcher.fetchPlayerData(2024);
        List<Player> activePlayers = dataFetcher.filterActivePlayers(players);
        
        StatsAnalyzer statsAnalyzer = new StatsAnalyzer();
        Map<String, Double> weights = statsAnalyzer.calculateWeights(activePlayers);
        
        Ranker ranker = new Ranker();
        Map<String, List<Player>> rankedByPosition = ranker.rankPlayersByPosition(activePlayers, weights, 15);
        
        assertNotNull(rankedByPosition, "Ranked players map should not be null");
        
        // Verify we have rankings for each position
        String[] positions = {"QB", "RB", "WR", "TE"};
        for (String position : positions) {
            assertTrue(rankedByPosition.containsKey(position), 
                "Should have rankings for position: " + position);
            List<Player> positionPlayers = rankedByPosition.get(position);
            assertNotNull(positionPlayers, "Position players list should not be null");
            assertTrue(positionPlayers.size() <= 15, 
                "Should have at most 15 players per position");
            
            // Verify players are sorted by score (descending)
            for (int i = 0; i < positionPlayers.size() - 1; i++) {
                assertTrue(positionPlayers.get(i).getScore() >= positionPlayers.get(i + 1).getScore(),
                    "Players should be sorted by score in descending order");
            }
        }
    }
    
    @Test
    public void testRankerRanksDefenses() {
        DataFetcher dataFetcher = new DataFetcher();
        List<Defense> defenses = dataFetcher.fetchDefenseData(2024);
        
        Ranker ranker = new Ranker();
        List<Defense> rankedDefenses = ranker.rankDefenses(defenses);
        
        assertNotNull(rankedDefenses, "Ranked defenses list should not be null");
        assertEquals(32, rankedDefenses.size(), "Should have all 32 defenses ranked");
        
        // Verify defenses are sorted by score (descending)
        for (int i = 0; i < rankedDefenses.size() - 1; i++) {
            assertTrue(rankedDefenses.get(i).getDefScore() >= rankedDefenses.get(i + 1).getDefScore(),
                "Defenses should be sorted by score in descending order");
        }
    }
    
    @Test
    public void testCompleteWorkflow() {
        // This test verifies the complete workflow runs without errors
        DataFetcher dataFetcher = new DataFetcher();
        StatsAnalyzer statsAnalyzer = new StatsAnalyzer();
        Ranker ranker = new Ranker();
        
        // Fetch and filter player data
        List<Player> players = dataFetcher.fetchPlayerData(2024);
        List<Player> activePlayers = dataFetcher.filterActivePlayers(players);
        
        // Calculate weights
        Map<String, Double> weights = statsAnalyzer.calculateWeights(activePlayers);
        
        // Rank players
        Map<String, List<Player>> rankedByPosition = ranker.rankPlayersByPosition(activePlayers, weights, 15);
        
        // Fetch and rank defenses
        List<Defense> defenses = dataFetcher.fetchDefenseData(2024);
        List<Defense> rankedDefenses = ranker.rankDefenses(defenses);
        
        // Verify results
        assertNotNull(rankedByPosition);
        assertNotNull(rankedDefenses);
        assertFalse(rankedByPosition.isEmpty());
        assertFalse(rankedDefenses.isEmpty());
    }
}

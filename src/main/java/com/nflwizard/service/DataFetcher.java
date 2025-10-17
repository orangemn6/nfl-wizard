package com.nflwizard.service;

import com.nflwizard.model.Player;
import com.nflwizard.model.Defense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fetches NFL player and defense data
 * In a production environment, this would connect to an NFL API
 * For now, generates realistic mock data
 */
public class DataFetcher {
    private static final Logger logger = LoggerFactory.getLogger(DataFetcher.class);
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    
    private static final String[] QB_NAMES = {
        "Mahomes", "Allen", "Burrow", "Herbert", "Jackson", "Prescott", "Hurts",
        "Lawrence", "Stroud", "Love", "Purdy", "Tagovailoa", "Goff", "Stafford"
    };
    
    private static final String[] RB_NAMES = {
        "McCaffrey", "Barkley", "Henry", "Gibbs", "Kamara", "Montgomery", 
        "Ekeler", "Jones", "Jacobs", "Chubb", "Cook", "Taylor", "Mixon", "Harris",
        "Stevenson", "Walker", "Pollard", "Williams"
    };
    
    private static final String[] WR_NAMES = {
        "Hill", "Jefferson", "Chase", "Lamb", "Adams", "Diggs", "Brown", "Evans",
        "Kupp", "Samuel", "Higgins", "Waddle", "Smith-Schuster", "Lockett", "Hopkins",
        "Metcalf", "McLaurin", "Johnson", "Pittman", "Wilson", "Smith", "Olave",
        "London", "Allen", "Moore"
    };
    
    private static final String[] TE_NAMES = {
        "Kelce", "Andrews", "Kittle", "Goedert", "Hockenson", "Pitts", "LaPorta",
        "Ertz", "Njoku", "Kmet", "Freiermuth", "Engram", "Gesicki", "Knox"
    };
    
    private static final String[] TEAM_CODES = {
        "SF", "BAL", "BUF", "DAL", "KC", "MIA", "PHI", "DET", "CLE", "NO",
        "LAC", "CIN", "JAX", "MIN", "SEA", "TB", "NYJ", "GB", "LAR", "PIT",
        "NE", "ATL", "LV", "IND", "TEN", "CHI", "DEN", "WAS", "NYG", "CAR",
        "ARI", "HOU"
    };
    
    private static final String[] TEAM_NAMES = {
        "49ers", "Ravens", "Bills", "Cowboys", "Chiefs", "Dolphins", "Eagles",
        "Lions", "Browns", "Saints", "Chargers", "Bengals", "Jaguars", "Vikings",
        "Seahawks", "Buccaneers", "Jets", "Packers", "Rams", "Steelers",
        "Patriots", "Falcons", "Raiders", "Colts", "Titans", "Bears", "Broncos",
        "Commanders", "Giants", "Panthers", "Cardinals", "Texans"
    };
    
    /**
     * Fetch player data for the given season
     */
    public List<Player> fetchPlayerData(int season) {
        logger.info("Fetching player data for season {}", season);
        List<Player> players = new ArrayList<>();
        
        // Generate QBs
        for (int i = 0; i < QB_NAMES.length; i++) {
            players.add(createPlayer(QB_NAMES[i], "QB", season, i + 1));
        }
        
        // Generate RBs
        for (int i = 0; i < RB_NAMES.length; i++) {
            players.add(createPlayer(RB_NAMES[i], "RB", season, i + 100));
        }
        
        // Generate WRs
        for (int i = 0; i < WR_NAMES.length; i++) {
            players.add(createPlayer(WR_NAMES[i], "WR", season, i + 200));
        }
        
        // Generate TEs
        for (int i = 0; i < TE_NAMES.length; i++) {
            players.add(createPlayer(TE_NAMES[i], "TE", season, i + 300));
        }
        
        logger.info("Fetched {} players", players.size());
        return players;
    }
    
    /**
     * Create a player with realistic stats based on position
     */
    private Player createPlayer(String lastName, String position, int season, int id) {
        Player player = new Player();
        player.setPlayerId("player_" + id);
        player.setPlayerName("Player " + lastName);
        player.setLastName(lastName);
        player.setPosition(position);
        player.setSeason(season);
        player.setSeasonType("REG");
        player.setGames(16 + random.nextInt(2));
        
        // Generate stats based on position
        double positionMultiplier = getPositionMultiplier(position);
        double variance = 0.7 + random.nextDouble() * 0.6; // 0.7 to 1.3
        
        // Base stats
        player.setTargets(50 + random.nextDouble() * 100 * positionMultiplier);
        player.setReceptions(player.getTargets() * (0.6 + random.nextDouble() * 0.2));
        player.setReceivingYards(player.getReceptions() * (8 + random.nextDouble() * 6));
        player.setReceivingTds(player.getReceptions() * (0.05 + random.nextDouble() * 0.05));
        player.setReceivingAirYards(player.getTargets() * (7 + random.nextDouble() * 5));
        player.setReceivingYardsAfterCatch(player.getReceivingYards() * (0.4 + random.nextDouble() * 0.2));
        player.setReceivingFirstDowns(player.getReceptions() * (0.3 + random.nextDouble() * 0.2));
        
        // Advanced stats (used for ranking)
        player.setTgtSh(0.1 + random.nextDouble() * 0.25 * positionMultiplier * variance);
        player.setAySh(0.08 + random.nextDouble() * 0.22 * positionMultiplier * variance);
        player.setYacSh(0.06 + random.nextDouble() * 0.18 * positionMultiplier * variance);
        player.setWoprY(0.3 + random.nextDouble() * 0.4 * positionMultiplier * variance);
        player.setRySh(0.05 + random.nextDouble() * 0.15 * positionMultiplier * variance);
        player.setRtdSh(0.03 + random.nextDouble() * 0.12 * positionMultiplier * variance);
        player.setRfdSh(0.04 + random.nextDouble() * 0.14 * positionMultiplier * variance);
        player.setRtdfdSh(0.02 + random.nextDouble() * 0.10 * positionMultiplier * variance);
        player.setDom(0.2 + random.nextDouble() * 0.3 * positionMultiplier * variance);
        player.setW8dom(0.15 + random.nextDouble() * 0.25 * positionMultiplier * variance);
        player.setYptmpa(1.5 + random.nextDouble() * 3.0 * positionMultiplier * variance);
        player.setPprSh(0.08 + random.nextDouble() * 0.20 * positionMultiplier * variance);
        
        // Fantasy points
        player.setFantasyPointsPpr(
            player.getReceptions() + 
            player.getReceivingYards() * 0.1 + 
            player.getReceivingTds() * 6
        );
        
        return player;
    }
    
    /**
     * Get position multiplier for stat generation
     */
    private double getPositionMultiplier(String position) {
        switch (position) {
            case "QB": return 0.8;
            case "RB": return 0.9;
            case "WR": return 1.2;
            case "TE": return 1.0;
            default: return 1.0;
        }
    }
    
    /**
     * Filter players to include only those with significant activity
     */
    public List<Player> filterActivePlayers(List<Player> players) {
        logger.info("Filtering active players from {} total players", players.size());
        List<Player> activePlayers = new ArrayList<>();
        
        for (Player player : players) {
            // Include players with targets and receptions
            if (player.getTargets() > 0 && player.getReceptions() > 0 && player.getPprSh() > 0) {
                activePlayers.add(player);
            }
        }
        
        logger.info("Filtered to {} active players", activePlayers.size());
        return activePlayers;
    }
    
    /**
     * Fetch defense data for the given season
     */
    public List<Defense> fetchDefenseData(int season) {
        logger.info("Fetching defense data for season {}", season);
        List<Defense> defenses = new ArrayList<>();
        
        for (int i = 0; i < TEAM_CODES.length; i++) {
            Defense defense = new Defense();
            defense.setTeam(TEAM_CODES[i]);
            defense.setTeamName(TEAM_NAMES[i]);
            defense.setSeason(season);
            defense.setSeasonType("REG");
            
            // Generate defense stats
            defense.setSacks(25 + random.nextDouble() * 30);
            defense.setFantasyPoints(80 + random.nextDouble() * 60);
            defense.setFantasyPointsPpr(defense.getFantasyPoints());
            
            defenses.add(defense);
        }
        
        logger.info("Fetched {} defenses", defenses.size());
        return defenses;
    }
}

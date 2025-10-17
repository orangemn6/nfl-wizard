package com.nflwizard.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an NFL team defense with their stats and calculated metrics
 */
public class Defense {
    private String team;
    private String teamName;
    private int season;
    private String seasonType;
    
    // Stats
    private double sacks;
    private double fantasyPoints;
    private double fantasyPointsPpr;
    
    // Calculated fields
    private Map<String, Double> normalizedStats;
    private double defScore;
    
    public Defense() {
        this.normalizedStats = new HashMap<>();
    }
    
    // Getters and Setters
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    
    public int getSeason() { return season; }
    public void setSeason(int season) { this.season = season; }
    
    public String getSeasonType() { return seasonType; }
    public void setSeasonType(String seasonType) { this.seasonType = seasonType; }
    
    public double getSacks() { return sacks; }
    public void setSacks(double sacks) { this.sacks = sacks; }
    
    public double getFantasyPoints() { return fantasyPoints; }
    public void setFantasyPoints(double fantasyPoints) { this.fantasyPoints = fantasyPoints; }
    
    public double getFantasyPointsPpr() { return fantasyPointsPpr; }
    public void setFantasyPointsPpr(double fantasyPointsPpr) { this.fantasyPointsPpr = fantasyPointsPpr; }
    
    public Map<String, Double> getNormalizedStats() { return normalizedStats; }
    public void setNormalizedStats(Map<String, Double> normalizedStats) { 
        this.normalizedStats = normalizedStats; 
    }
    
    public void setNormalizedStat(String statName, double value) {
        this.normalizedStats.put(statName, value);
    }
    
    public double getDefScore() { return defScore; }
    public void setDefScore(double defScore) { this.defScore = defScore; }
    
    /**
     * Get stat value by name
     */
    public Double getStatValue(String statName) {
        switch (statName) {
            case "sacks": return sacks;
            case "fantasy_points": return fantasyPoints;
            case "fantasy_points_ppr": return fantasyPointsPpr;
            default: return null;
        }
    }
}

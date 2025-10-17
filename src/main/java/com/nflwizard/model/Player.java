package com.nflwizard.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an NFL player with their stats and calculated metrics
 */
public class Player {
    private String playerId;
    private String playerName;
    private String lastName;
    private String position;
    private int season;
    private String seasonType;
    
    // Stats
    private double targets;
    private double receptions;
    private double receivingYards;
    private double receivingTds;
    private double receivingAirYards;
    private double receivingYardsAfterCatch;
    private double receivingFirstDowns;
    private double targetShare;
    private double airYardsShare;
    private double wopr;
    private double fantasyPointsPpr;
    private int games;
    
    // Advanced stats used for ranking
    private double tgtSh;
    private double aySh;
    private double yacSh;
    private double woprY;
    private double rySh;
    private double rtdSh;
    private double rfdSh;
    private double rtdfdSh;
    private double dom;
    private double w8dom;
    private double yptmpa;
    private double pprSh;
    
    // Calculated fields
    private Map<String, Double> normalizedStats;
    private double score;
    
    public Player() {
        this.normalizedStats = new HashMap<>();
    }
    
    // Getters and Setters
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public int getSeason() { return season; }
    public void setSeason(int season) { this.season = season; }
    
    public String getSeasonType() { return seasonType; }
    public void setSeasonType(String seasonType) { this.seasonType = seasonType; }
    
    public double getTargets() { return targets; }
    public void setTargets(double targets) { this.targets = targets; }
    
    public double getReceptions() { return receptions; }
    public void setReceptions(double receptions) { this.receptions = receptions; }
    
    public double getReceivingYards() { return receivingYards; }
    public void setReceivingYards(double receivingYards) { this.receivingYards = receivingYards; }
    
    public double getReceivingTds() { return receivingTds; }
    public void setReceivingTds(double receivingTds) { this.receivingTds = receivingTds; }
    
    public double getReceivingAirYards() { return receivingAirYards; }
    public void setReceivingAirYards(double receivingAirYards) { this.receivingAirYards = receivingAirYards; }
    
    public double getReceivingYardsAfterCatch() { return receivingYardsAfterCatch; }
    public void setReceivingYardsAfterCatch(double receivingYardsAfterCatch) { 
        this.receivingYardsAfterCatch = receivingYardsAfterCatch; 
    }
    
    public double getReceivingFirstDowns() { return receivingFirstDowns; }
    public void setReceivingFirstDowns(double receivingFirstDowns) { 
        this.receivingFirstDowns = receivingFirstDowns; 
    }
    
    public double getTargetShare() { return targetShare; }
    public void setTargetShare(double targetShare) { this.targetShare = targetShare; }
    
    public double getAirYardsShare() { return airYardsShare; }
    public void setAirYardsShare(double airYardsShare) { this.airYardsShare = airYardsShare; }
    
    public double getWopr() { return wopr; }
    public void setWopr(double wopr) { this.wopr = wopr; }
    
    public double getFantasyPointsPpr() { return fantasyPointsPpr; }
    public void setFantasyPointsPpr(double fantasyPointsPpr) { this.fantasyPointsPpr = fantasyPointsPpr; }
    
    public int getGames() { return games; }
    public void setGames(int games) { this.games = games; }
    
    // Advanced stats
    public double getTgtSh() { return tgtSh; }
    public void setTgtSh(double tgtSh) { this.tgtSh = tgtSh; }
    
    public double getAySh() { return aySh; }
    public void setAySh(double aySh) { this.aySh = aySh; }
    
    public double getYacSh() { return yacSh; }
    public void setYacSh(double yacSh) { this.yacSh = yacSh; }
    
    public double getWoprY() { return woprY; }
    public void setWoprY(double woprY) { this.woprY = woprY; }
    
    public double getRySh() { return rySh; }
    public void setRySh(double rySh) { this.rySh = rySh; }
    
    public double getRtdSh() { return rtdSh; }
    public void setRtdSh(double rtdSh) { this.rtdSh = rtdSh; }
    
    public double getRfdSh() { return rfdSh; }
    public void setRfdSh(double rfdSh) { this.rfdSh = rfdSh; }
    
    public double getRtdfdSh() { return rtdfdSh; }
    public void setRtdfdSh(double rtdfdSh) { this.rtdfdSh = rtdfdSh; }
    
    public double getDom() { return dom; }
    public void setDom(double dom) { this.dom = dom; }
    
    public double getW8dom() { return w8dom; }
    public void setW8dom(double w8dom) { this.w8dom = w8dom; }
    
    public double getYptmpa() { return yptmpa; }
    public void setYptmpa(double yptmpa) { this.yptmpa = yptmpa; }
    
    public double getPprSh() { return pprSh; }
    public void setPprSh(double pprSh) { this.pprSh = pprSh; }
    
    // Calculated fields
    public Map<String, Double> getNormalizedStats() { return normalizedStats; }
    public void setNormalizedStats(Map<String, Double> normalizedStats) { 
        this.normalizedStats = normalizedStats; 
    }
    
    public void setNormalizedStat(String statName, double value) {
        this.normalizedStats.put(statName, value);
    }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    /**
     * Get stat value by name
     */
    public Double getStatValue(String statName) {
        switch (statName) {
            case "tgt_sh": return tgtSh;
            case "ay_sh": return aySh;
            case "yac_sh": return yacSh;
            case "wopr_y": return woprY;
            case "ry_sh": return rySh;
            case "rtd_sh": return rtdSh;
            case "rfd_sh": return rfdSh;
            case "rtdfd_sh": return rtdfdSh;
            case "dom": return dom;
            case "w8dom": return w8dom;
            case "yptmpa": return yptmpa;
            case "ppr_sh": return pprSh;
            default: return null;
        }
    }
}

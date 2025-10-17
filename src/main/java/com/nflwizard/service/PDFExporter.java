package com.nflwizard.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.nflwizard.model.Defense;
import com.nflwizard.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Exports player and defense rankings to a multi-page PDF
 */
public class PDFExporter {
    private static final Logger logger = LoggerFactory.getLogger(PDFExporter.class);
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
    private static final Font HIGHLIGHT_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(0, 100, 0));
    
    /**
     * Export rankings to PDF
     */
    public void exportToPDF(Map<String, List<Player>> rankedByPosition, List<Defense> rankedDefenses, String outputFile) {
        logger.info("Exporting rankings to PDF: {}", outputFile);
        
        try {
            Document document = new Document(PageSize.LETTER);
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            
            // Export each position on its own page
            String[] positions = {"QB", "RB", "WR", "TE"};
            for (String position : positions) {
                List<Player> players = rankedByPosition.get(position);
                if (players != null && !players.isEmpty()) {
                    exportPositionPage(document, position, players);
                    document.newPage();
                }
            }
            
            // Export defenses
            if (rankedDefenses != null && !rankedDefenses.isEmpty()) {
                exportDefensePage(document, rankedDefenses);
            }
            
            document.close();
            logger.info("PDF export complete: {}", outputFile);
        } catch (Exception e) {
            logger.error("Error exporting PDF", e);
            throw new RuntimeException("Failed to export PDF", e);
        }
    }
    
    /**
     * Export a page for a specific position
     */
    private void exportPositionPage(Document document, String position, List<Player> players) throws DocumentException {
        // Add title
        Paragraph title = new Paragraph("Top 15 " + position + "s", TITLE_FONT);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Determine top PPR share threshold for highlighting (top 20%)
        int highlightThreshold = Math.max(1, players.size() / 5);
        double minPprShForHighlight = players.size() > highlightThreshold 
            ? players.get(highlightThreshold - 1).getPprSh() 
            : 0;
        
        // Add player rankings (top 15)
        int count = 0;
        for (Player player : players) {
            if (count >= 15) break;
            
            String line = String.format("%s - %.4f", player.getLastName(), player.getScore());
            
            // Highlight top PPR share players
            Font font = (player.getPprSh() >= minPprShForHighlight) ? HIGHLIGHT_FONT : NORMAL_FONT;
            
            Paragraph playerLine = new Paragraph(line, font);
            playerLine.setSpacingAfter(5);
            document.add(playerLine);
            
            count++;
        }
    }
    
    /**
     * Export a page for team defenses
     */
    private void exportDefensePage(Document document, List<Defense> defenses) throws DocumentException {
        // Add title
        Paragraph title = new Paragraph("Top 15 Team Defenses", TITLE_FONT);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add defense rankings (top 15)
        int count = 0;
        for (Defense defense : defenses) {
            if (count >= 15) break;
            
            String line = String.format("%s - %.4f", defense.getTeamName(), defense.getDefScore());
            
            Paragraph defenseLine = new Paragraph(line, NORMAL_FONT);
            defenseLine.setSpacingAfter(5);
            document.add(defenseLine);
            
            count++;
        }
    }
}

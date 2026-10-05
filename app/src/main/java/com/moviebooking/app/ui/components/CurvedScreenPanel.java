package com.moviebooking.app.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class CurvedScreenPanel extends JPanel {

    public CurvedScreenPanel() {
        setPreferredSize(new Dimension(500, 60));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw curved screen arc with glowing gradient
        g2.setColor(new Color(37, 99, 235, 40));
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        QuadCurve2D curveGlow = new QuadCurve2D.Float(40, 45, w / 2f, 15, w - 40, 45);
        g2.draw(curveGlow);

        g2.setColor(new Color(37, 99, 235));
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        QuadCurve2D curve = new QuadCurve2D.Float(40, 45, w / 2f, 15, w - 40, 45);
        g2.draw(curve);

        // Screen Label
        g2.setColor(new Color(100, 116, 139));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String label = "— ALL EYES THIS WAY • CINEMA SCREEN —";
        int textX = (w - fm.stringWidth(label)) / 2;
        g2.drawString(label, textX, 58);

        g2.dispose();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author imani
 */
public class DashboardPanel extends JPanel{
    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 248, 252));

                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerPanel.setOpaque(false);

        ImageIcon logoIcon = util.ImageHelper.loadScaledIcon("/images/logo.png", 32, 32);
        if (logoIcon != null) {
            headerPanel.add(new JLabel(logoIcon));
        }

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(33, 78, 121));
        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 15, 15));
        cards.setOpaque(false);

        cards.add(makeCard("Total Customers",    String.valueOf(countQuery("SELECT COUNT(*) FROM Customer")),    new Color(52, 152, 219)));
        cards.add(makeCard("Total Products",     String.valueOf(countQuery("SELECT COUNT(*) FROM Product")),     new Color(46, 204, 113)));
        cards.add(makeCard("Total Orders",       String.valueOf(countQuery("SELECT COUNT(*) FROM SalesOrder")),  new Color(155, 89, 182)));
        cards.add(makeCard("Total Revenue (LKR)",formatMoney(sumQuery("SELECT IFNULL(SUM(TotalAmount),0) FROM SalesOrder")), new Color(230, 126, 34)));
        cards.add(makeCard("Low Stock Items (<10)", String.valueOf(countQuery("SELECT COUNT(*) FROM Product WHERE StockQuantity < 10")), new Color(231, 76, 60)));
        cards.add(makeCard("Customers Today",    String.valueOf(countQuery("SELECT COUNT(*) FROM Customer WHERE CreatedDate = CURDATE()")), new Color(26, 188, 156)));

        add(cards, BorderLayout.CENTER);
    }

    private JPanel makeCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(color);
        header.setPreferredSize(new Dimension(0, 40));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.CENTER);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setForeground(color);
        lblValue.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private int countQuery(String sql) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("Dashboard count error: " + e.getMessage());
        }
        return 0;
    }

    private double sumQuery(String sql) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.err.println("Dashboard sum error: " + e.getMessage());
        }
        return 0;
    }

    private String formatMoney(double v) {
        return String.format("%,.2f", v);
    }
}

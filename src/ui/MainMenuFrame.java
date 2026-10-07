/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author imani
 */
public class MainMenuFrame extends JFrame {
    public MainMenuFrame() {
        setTitle("Furniture Store - Main Menu");
        setSize(1000, 650);
        setLocationRelativeTo(null);
                util.ImageHelper.setWindowIcon(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
      
    }

    private void initComponents() {
        // Menu bar
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        logoutItem.addActionListener(e -> {
            SessionManager.getInstance().logout();
            new LoginFrame().setVisible(true);
            dispose();
        });
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu manageMenu = new JMenu("Manage");
        JMenuItem custItem = new JMenuItem("Customers");
        JMenuItem prodItem = new JMenuItem("Products");
        JMenuItem orderItem = new JMenuItem("New Sales Order");
        custItem.addActionListener(e -> new CustomerFrame().setVisible(true));
        prodItem.addActionListener(e -> new ProductFrame().setVisible(true));
        orderItem.addActionListener(e -> new SalesOrderFrame().setVisible(true));
        manageMenu.add(custItem);
        manageMenu.add(prodItem);
        manageMenu.addSeparator();
        manageMenu.add(orderItem);

        JMenu reportMenu = new JMenu("Reports");
        String[] reports = {
            "1. Sales Summary",
            "2. Top Selling Products",
            "3. Customer Order History",
            "4. Low Stock Alert",
            "5. Monthly Revenue"
        };
        for (int i = 0; i < reports.length; i++) {
            final int idx = i + 1;
            JMenuItem item = new JMenuItem(reports[i]);
            item.addActionListener(e -> openReport(idx));
            reportMenu.add(item);
        }

        bar.add(fileMenu);
        bar.add(manageMenu);
        bar.add(reportMenu);
        setJMenuBar(bar);

        // Body — embedded dashboard panel
        DashboardPanel dashboardPanel = new DashboardPanel();
        add(dashboardPanel, BorderLayout.CENTER);

        // Status bar
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        status.setBackground(new Color(230, 230, 230));
        String userName = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getFullName() : "Guest";
        status.add(new JLabel("Logged in as: " + userName), BorderLayout.WEST);
        status.add(new JLabel("Furniture Store Sales System v1.0"), BorderLayout.EAST);
        add(status, BorderLayout.SOUTH);
    }

        private void openReport(int index) {
        String file;
        switch (index) {
            case 1: file = "SalesSummary.jrxml";         break;
            case 2: file = "TopSellingProducts.jrxml";   break;
            case 3: file = "CustomerOrderHistory.jrxml"; break;
            case 4: file = "LowStockAlert.jrxml";        break;
            case 5: file = "MonthlyRevenue.jrxml";       break;
            default: return;
        }
        reports.ReportRunner.show(this, file);
    }
}

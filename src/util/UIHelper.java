package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UIHelper {

    public static final Color PRIMARY = new Color(33, 78, 121);
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color DANGER  = new Color(231, 76, 60);
    public static final Color WARNING = new Color(230, 126, 34);
    public static final Color LIGHT   = new Color(245, 248, 252);

    public static void styleButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(90, 32));
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Force the color to stick even on Windows L&F
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        // Ensure foreground stays white even when disabled
        b.setDisabledSelectedIcon(null);
        b.setForeground(Color.WHITE);
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 240));
        table.setGridColor(new Color(220, 220, 220));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
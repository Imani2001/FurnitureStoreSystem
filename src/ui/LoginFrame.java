/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import dao.UserDAO;
import model.User;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import util.ImageHelper;

/**
 *
 * @author imani
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    public LoginFrame() {
        setTitle("Furniture Store - Login");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(240, 245, 250));

        // Header
                // Header with logo
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        header.setBackground(new Color(33, 78, 121));
        header.setPreferredSize(new Dimension(0, 90));

        ImageIcon logoIcon = ImageHelper.loadScaledIcon("/images/logo.png", 50, 50);
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            header.add(logoLabel);
        }

        JLabel title = new JLabel("FURNITURE STORE");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(240, 245, 250));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Username:"), g);
        g.gridx = 1;
        txtUsername = new JTextField(15);
        form.add(txtUsername, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Password:"), g);
        g.gridx = 1;
        txtPassword = new JPasswordField(15);
        form.add(txtPassword, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(240, 245, 250));
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 32));
        btnLogin.setBackground(new Color(33, 78, 121));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        btnExit = new JButton("Exit");
        btnExit.setPreferredSize(new Dimension(100, 32));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        form.add(btnPanel, g);

        // Hint
        g.gridy = 3;
        JLabel hint = new JLabel("Default: admin / admin123");
        hint.setForeground(Color.GRAY);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(hint, g);

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);

        add(main);

        // Events
        btnLogin.addActionListener(this::doLogin);
        btnExit.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnLogin);
        txtPassword.addActionListener(this::doLogin);
    }

    private void doLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password.",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.login(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                return;
            }

            // Store logged user
            SessionManager.getInstance().setCurrentUser(user);

            // Open main menu
            new MainMenuFrame().setVisible(true);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        public static void main(String[] args) {
                    
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
        
        util.GlobalExceptionHandler.install();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

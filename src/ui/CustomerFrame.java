package ui;

import dao.CustomerDAO;
import model.Customer;
import util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class CustomerFrame extends JFrame {

    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress, txtCity, txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    private int selectedCustomerId = -1;

    public CustomerFrame() {
        setTitle("Customer Management");
        setSize(1100, 720);
        setLocationRelativeTo(null);
                util.ImageHelper.setWindowIcon(this);
        

        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ============ TOP: Title ============
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIHelper.PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Customer Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // ============ CENTER: Split (Form left, Table right) ============
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(UIHelper.LIGHT);

        // -------- LEFT: Form --------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        txtFirstName = new JTextField(15);
        txtLastName  = new JTextField(15);
        txtEmail     = new JTextField(15);
        txtPhone     = new JTextField(15);
        txtAddress   = new JTextField(15);
        txtCity      = new JTextField(15);

        addRow(formPanel, g, 0, "First Name *", txtFirstName);
        addRow(formPanel, g, 1, "Last Name *",  txtLastName);
        addRow(formPanel, g, 2, "Email",        txtEmail);
        addRow(formPanel, g, 3, "Phone *",      txtPhone);
        addRow(formPanel, g, 4, "Address",      txtAddress);
        addRow(formPanel, g, 5, "City",         txtCity);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnPanel.setBackground(Color.WHITE);

        btnSave   = new JButton("Save");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");

        UIHelper.styleButton(btnSave, UIHelper.SUCCESS);
        UIHelper.styleButton(btnUpdate, UIHelper.PRIMARY);
        UIHelper.styleButton(btnDelete, UIHelper.DANGER);
        UIHelper.styleButton(btnClear, UIHelper.WARNING);

        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        // -------- RIGHT: Table + Search --------
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Customer List"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(15);
        searchPanel.add(txtSearch);
        btnSearch  = new JButton("Search");
        btnRefresh = new JButton("Refresh");
        UIHelper.styleButton(btnSearch, UIHelper.PRIMARY);
        UIHelper.styleButton(btnRefresh, UIHelper.WARNING);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        model = new DefaultTableModel(
                new Object[]{"ID", "First Name", "Last Name", "Email", "Phone", "City"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);

        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);

        centerPanel.add(formPanel);
        centerPanel.add(tablePanel);
        add(centerPanel, BorderLayout.CENTER);

        // ============ EVENTS ============
        btnSave.addActionListener(e -> saveCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchCustomers());
        btnRefresh.addActionListener(e -> loadCustomers());
        txtSearch.addActionListener(e -> searchCustomers());

        // Row click → fill form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedCustomerId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txtFirstName.setText(str(model.getValueAt(row, 1)));
                    txtLastName.setText(str(model.getValueAt(row, 2)));
                    txtEmail.setText(str(model.getValueAt(row, 3)));
                    txtPhone.setText(str(model.getValueAt(row, 4)));
                    txtCity.setText(str(model.getValueAt(row, 5)));

                    // Address not in table — fetch on demand isn't necessary; leave as blank or refetch
                    txtAddress.setText("");
                }
            }
        });
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(field, g);
    }

    // ============ CRUD ============

    private boolean validateForm() {
        if (txtFirstName.getText().trim().isEmpty()) {
            UIHelper.showError(this, "First Name is required.");
            txtFirstName.requestFocus();
            return false;
        }
        if (txtLastName.getText().trim().isEmpty()) {
            UIHelper.showError(this, "Last Name is required.");
            txtLastName.requestFocus();
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            UIHelper.showError(this, "Phone is required.");
            txtPhone.requestFocus();
            return false;
        }
        if (!txtPhone.getText().trim().matches("\\d{10}")) {
            UIHelper.showError(this, "Phone must be exactly 10 digits (e.g., 0771234567).");
            txtPhone.requestFocus();
            return false;
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            UIHelper.showError(this, "Invalid email format.");
            txtEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Customer readForm() {
        Customer c = new Customer();
        c.setCustomerId(selectedCustomerId);
        c.setFirstName(txtFirstName.getText().trim());
        c.setLastName(txtLastName.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        c.setCity(txtCity.getText().trim());
        return c;
    }

    private void saveCustomer() {
        if (!validateForm()) return;
        try {
            new CustomerDAO().add(readForm());
            UIHelper.showInfo(this, "Customer added successfully.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Save failed: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            UIHelper.showError(this, "Please select a customer from the table first.");
            return;
        }
        if (!validateForm()) return;
        try {
            new CustomerDAO().update(readForm());
            UIHelper.showInfo(this, "Customer updated successfully.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            UIHelper.showError(this, "Please select a customer from the table first.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this customer? This cannot be undone.")) return;
        try {
            new CustomerDAO().delete(selectedCustomerId);
            UIHelper.showInfo(this, "Customer deleted.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            // Likely foreign key violation (customer has orders)
            UIHelper.showError(this,
                "Cannot delete: this customer has existing orders.\nDetails: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedCustomerId = -1;
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        table.clearSelection();
        txtFirstName.requestFocus();
    }

    private void loadCustomers() {
        try {
            model.setRowCount(0);
            List<Customer> list = new CustomerDAO().getAll();
            for (Customer c : list) {
                model.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getCity()
                });
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Load failed: " + ex.getMessage());
        }
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadCustomers(); return; }
        try {
            model.setRowCount(0);
            List<Customer> list = new CustomerDAO().search(keyword);
            for (Customer c : list) {
                model.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getCity()
                });
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Search failed: " + ex.getMessage());
        }
    }
}
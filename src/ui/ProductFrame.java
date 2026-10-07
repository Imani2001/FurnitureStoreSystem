package ui;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;
import util.ImageHelper;
import util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class ProductFrame extends JFrame {

    private JTextField txtName, txtPrice, txtStock, txtSupplier, txtSearch;
    private JComboBox<Category> cmbCategory;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    private int selectedProductId = -1;

    public ProductFrame() {
        setTitle("Product Management");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        ImageHelper.setWindowIcon(this);

        initComponents();
        loadCategories();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ============ TOP HEADER ============
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIHelper.PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // ============ CENTER: Split ============
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(UIHelper.LIGHT);

        // -------- LEFT: Form --------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        txtName     = new JTextField(15);
        txtPrice    = new JTextField(15);
        txtStock    = new JTextField(15);
        txtSupplier = new JTextField(15);
        cmbCategory = new JComboBox<>();

        addRow(formPanel, g, 0, "Product Name *", txtName);
        addRow(formPanel, g, 1, "Category *",     cmbCategory);
        addRow(formPanel, g, 2, "Price (LKR) *",  txtPrice);
        addRow(formPanel, g, 3, "Stock Qty *",    txtStock);
        addRow(formPanel, g, 4, "Supplier",       txtSupplier);

        // Buttons — 2x2 grid
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        btnSave   = new JButton("Save");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear  = new JButton("Clear");

        UIHelper.styleButton(btnSave, UIHelper.SUCCESS);
        UIHelper.styleButton(btnUpdate, UIHelper.PRIMARY);
        UIHelper.styleButton(btnDelete, UIHelper.DANGER);
        UIHelper.styleButton(btnClear, UIHelper.WARNING);

        for (JButton b : new JButton[]{btnSave, btnUpdate, btnDelete, btnClear}) {
            b.setPreferredSize(null);
        }

        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        // -------- RIGHT: Table + Search --------
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Product List"));

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
                new Object[]{"ID", "Product Name", "Category", "Price", "Stock", "Supplier"}, 0) {
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
        btnSave.addActionListener(e -> saveProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchProducts());
        btnRefresh.addActionListener(e -> loadProducts());
        txtSearch.addActionListener(e -> searchProducts());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedProductId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txtName.setText(str(model.getValueAt(row, 1)));
                    // Select matching category
                    String catName = str(model.getValueAt(row, 2));
                    for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                        if (cmbCategory.getItemAt(i).getCategoryName().equals(catName)) {
                            cmbCategory.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtPrice.setText(str(model.getValueAt(row, 3)));
                    txtStock.setText(str(model.getValueAt(row, 4)));
                    txtSupplier.setText(str(model.getValueAt(row, 5)));
                }
            }
        });
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(field, g);
    }

    // ============ CRUD ============

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            UIHelper.showError(this, "Product Name is required.");
            txtName.requestFocus(); return false;
        }
        if (cmbCategory.getSelectedItem() == null) {
            UIHelper.showError(this, "Please select a Category.");
            return false;
        }
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            if (price <= 0) {
                UIHelper.showError(this, "Price must be greater than 0.");
                txtPrice.requestFocus(); return false;
            }
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Price must be a valid number.");
            txtPrice.requestFocus(); return false;
        }
        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                UIHelper.showError(this, "Stock cannot be negative.");
                txtStock.requestFocus(); return false;
            }
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Stock must be a whole number.");
            txtStock.requestFocus(); return false;
        }
        return true;
    }

    private Product readForm() {
        Product p = new Product();
        p.setProductId(selectedProductId);
        p.setProductName(txtName.getText().trim());
        Category cat = (Category) cmbCategory.getSelectedItem();
        p.setCategoryId(cat.getCategoryId());
        p.setPrice(Double.parseDouble(txtPrice.getText().trim()));
        p.setStockQuantity(Integer.parseInt(txtStock.getText().trim()));
        p.setSupplierName(txtSupplier.getText().trim());
        return p;
    }

    private void saveProduct() {
        if (!validateForm()) return;
        try {
            new ProductDAO().add(readForm());
            UIHelper.showInfo(this, "Product added successfully.");
            clearForm();
            loadProducts();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Save failed: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        if (selectedProductId == -1) {
            UIHelper.showError(this, "Please select a product from the table first.");
            return;
        }
        if (!validateForm()) return;
        try {
            new ProductDAO().update(readForm());
            UIHelper.showInfo(this, "Product updated successfully.");
            clearForm();
            loadProducts();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        if (selectedProductId == -1) {
            UIHelper.showError(this, "Please select a product from the table first.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this product? This cannot be undone.")) return;
        try {
            new ProductDAO().delete(selectedProductId);
            UIHelper.showInfo(this, "Product deleted.");
            clearForm();
            loadProducts();
        } catch (SQLException ex) {
            UIHelper.showError(this,
                "Cannot delete: this product is used in existing orders.\nDetails: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedProductId = -1;
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtSupplier.setText("");
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        table.clearSelection();
        txtName.requestFocus();
    }

    private void loadCategories() {
        try {
            cmbCategory.removeAllItems();
            List<Category> list = new CategoryDAO().getAll();
            for (Category c : list) cmbCategory.addItem(c);
        } catch (SQLException ex) {
            UIHelper.showError(this, "Load categories failed: " + ex.getMessage());
        }
    }

    private void loadProducts() {
        try {
            model.setRowCount(0);
            List<Product> list = new ProductDAO().getAll();
            for (Product p : list) {
                model.addRow(new Object[]{
                        p.getProductId(),
                        p.getProductName(),
                        p.getCategoryName(),
                        String.format("%,.2f", p.getPrice()),
                        p.getStockQuantity(),
                        p.getSupplierName()
                });
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Load failed: " + ex.getMessage());
        }
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadProducts(); return; }
        try {
            model.setRowCount(0);
            List<Product> list = new ProductDAO().search(keyword);
            for (Product p : list) {
                model.addRow(new Object[]{
                        p.getProductId(),
                        p.getProductName(),
                        p.getCategoryName(),
                        String.format("%,.2f", p.getPrice()),
                        p.getStockQuantity(),
                        p.getSupplierName()
                });
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Search failed: " + ex.getMessage());
        }
    }
}
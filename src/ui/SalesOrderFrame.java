package ui;

import dao.CustomerDAO;
import dao.ProductDAO;
import dao.SalesOrderDAO;
import model.Customer;
import model.Product;
import model.SalesOrder;
import util.ImageHelper;
import util.SessionManager;
import util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesOrderFrame extends JFrame {

    // ---- Cart / New Order Tab components
    private JComboBox<Customer> cmbCustomer;
    private JComboBox<Product> cmbProduct;
    private JTextField txtQty;
    private JTextField txtOrderDate;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel lblSubTotal, lblTax, lblTotal;
    private JButton btnAddItem, btnRemoveItem, btnClearCart, btnSaveOrder;

    // ---- Order List Tab components
    private JTable orderTable;
    private DefaultTableModel orderModel;
    private JButton btnRefreshOrders, btnDeleteOrder;

    // ---- Data
    private final List<Object[]> cartItems = new ArrayList<>(); // {productId, name, qty, unitPrice, subtotal}
    private JTabbedPane tabs;

    public SalesOrderFrame() {
        setTitle("Sales Order Management");
        setSize(1150, 750);
        setLocationRelativeTo(null);
        ImageHelper.setWindowIcon(this);

        initComponents();
        loadCustomers();
        loadProducts();
        loadOrders();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ====== TOP HEADER ======
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIHelper.PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Sales Order Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // ====== TABS ======
        tabs = new JTabbedPane();
        tabs.addTab("New Order", buildNewOrderTab());
        tabs.addTab("Order List", buildOrderListTab());
        add(tabs, BorderLayout.CENTER);
    }

    // =================================================================
    // TAB 1: NEW ORDER
    // =================================================================
    private JPanel buildNewOrderTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(UIHelper.LIGHT);

        // -------- TOP: Customer + Date --------
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Order Information"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        cmbCustomer = new JComboBox<>();
        cmbCustomer.setPreferredSize(new Dimension(250, 28));

        txtOrderDate = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtOrderDate.setEditable(false);
        txtOrderDate.setPreferredSize(new Dimension(150, 28));

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        infoPanel.add(new JLabel("Customer *"), g);
        g.gridx = 1; g.weightx = 1;
        infoPanel.add(cmbCustomer, g);

        g.gridx = 2; g.weightx = 0;
        infoPanel.add(new JLabel("Order Date"), g);
        g.gridx = 3; g.weightx = 0;
        infoPanel.add(txtOrderDate, g);

        // Cashier
        g.gridx = 4; g.weightx = 0;
        infoPanel.add(new JLabel("Cashier"), g);
        g.gridx = 5; g.weightx = 1;
        String cashier = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getFullName() : "-";
        JLabel lblCashier = new JLabel(cashier);
        lblCashier.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(lblCashier, g);

        panel.add(infoPanel, BorderLayout.NORTH);

        // -------- MIDDLE: Add Item + Cart --------
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        // Add Item row
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        addItemPanel.setBackground(Color.WHITE);
        addItemPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));

        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new Dimension(300, 28));
        txtQty = new JTextField("1", 5);

        btnAddItem = new JButton("Add to Cart");
        btnRemoveItem = new JButton("Remove Selected");
        btnClearCart = new JButton("Clear Cart");

        UIHelper.styleButton(btnAddItem, UIHelper.SUCCESS);
        UIHelper.styleButton(btnRemoveItem, UIHelper.DANGER);
        UIHelper.styleButton(btnClearCart, UIHelper.WARNING);

        addItemPanel.add(new JLabel("Product:"));
        addItemPanel.add(cmbProduct);
        addItemPanel.add(new JLabel("Qty:"));
        addItemPanel.add(txtQty);
        addItemPanel.add(btnAddItem);
        addItemPanel.add(btnRemoveItem);
        addItemPanel.add(btnClearCart);

        centerPanel.add(addItemPanel, BorderLayout.NORTH);

        // Cart table
        cartModel = new DefaultTableModel(
                new Object[]{"Product ID", "Product Name", "Unit Price", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        UIHelper.styleTable(cartTable);
        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Cart Items"));
        centerPanel.add(scroll, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // -------- BOTTOM: Totals + Save --------
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(UIHelper.LIGHT);

        // Totals panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        totalsPanel.add(new JLabel("Sub Total:"));
        lblSubTotal = new JLabel("LKR 0.00");
        lblSubTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(lblSubTotal);

        totalsPanel.add(new JLabel("Tax (10%):"));
        lblTax = new JLabel("LKR 0.00");
        lblTax.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(lblTax);

        totalsPanel.add(new JLabel("TOTAL:"));
        lblTotal = new JLabel("LKR 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 128, 0));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(lblTotal);

        JPanel totalsWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalsWrapper.setBackground(UIHelper.LIGHT);
        totalsWrapper.add(totalsPanel);

        // Save button
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        savePanel.setBackground(UIHelper.LIGHT);
        btnSaveOrder = new JButton("SAVE ORDER");
        btnSaveOrder.setPreferredSize(new Dimension(180, 45));
        btnSaveOrder.setBackground(new Color(0, 128, 0));
        btnSaveOrder.setForeground(Color.WHITE);
        btnSaveOrder.setFont(new Font("Arial", Font.BOLD, 14));
        btnSaveOrder.setFocusPainted(false);
        savePanel.add(btnSaveOrder);

        bottomPanel.add(savePanel, BorderLayout.WEST);
        bottomPanel.add(totalsWrapper, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // ====== EVENTS ======
        btnAddItem.addActionListener(e -> addItemToCart());
        btnRemoveItem.addActionListener(e -> removeSelectedItem());
        btnClearCart.addActionListener(e -> clearCart());
        btnSaveOrder.addActionListener(e -> saveOrder());

        return panel;
    }

    // =================================================================
    // TAB 2: ORDER LIST
    // =================================================================
    private JPanel buildOrderListTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(UIHelper.LIGHT);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createTitledBorder("Orders"));

        btnRefreshOrders = new JButton("Refresh");
        btnDeleteOrder = new JButton("Delete Selected");
        UIHelper.styleButton(btnRefreshOrders, UIHelper.PRIMARY);
        UIHelper.styleButton(btnDeleteOrder, UIHelper.DANGER);

        topBar.add(btnRefreshOrders);
        topBar.add(btnDeleteOrder);

        orderModel = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Date", "Total", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        UIHelper.styleTable(orderTable);
        JScrollPane scroll = new JScrollPane(orderTable);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        btnRefreshOrders.addActionListener(e -> loadOrders());
        btnDeleteOrder.addActionListener(e -> deleteSelectedOrder());

        return panel;
    }

    // =================================================================
    // DATA LOADING
    // =================================================================
    private void loadCustomers() {
        try {
            cmbCustomer.removeAllItems();
            for (Customer c : new CustomerDAO().getAll()) cmbCustomer.addItem(c);
        } catch (SQLException e) {
            UIHelper.showError(this, "Load customers failed: " + e.getMessage());
        }
    }

    private void loadProducts() {
        try {
            cmbProduct.removeAllItems();
            for (Product p : new ProductDAO().getAll()) cmbProduct.addItem(p);
        } catch (SQLException e) {
            UIHelper.showError(this, "Load products failed: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try {
            orderModel.setRowCount(0);
            for (SalesOrder so : new SalesOrderDAO().getAll()) {
                orderModel.addRow(new Object[]{
                        so.getOrderId(),
                        so.getCustomerName(),
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(so.getOrderDate()),
                        String.format("%,.2f", so.getTotalAmount()),
                        so.getStatus()
                });
            }
        } catch (SQLException e) {
            UIHelper.showError(this, "Load orders failed: " + e.getMessage());
        }
    }

    // =================================================================
    // CART OPERATIONS
    // =================================================================
    private void addItemToCart() {
        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected == null) {
            UIHelper.showError(this, "No product selected.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) {
                UIHelper.showError(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Quantity must be a whole number.");
            return;
        }

        if (qty > selected.getStockQuantity()) {
            UIHelper.showError(this,
                "Not enough stock. Available: " + selected.getStockQuantity());
            return;
        }

        // If product already in cart, increase quantity
        for (Object[] row : cartItems) {
            if ((int) row[0] == selected.getProductId()) {
                int newQty = (int) row[2] + qty;
                if (newQty > selected.getStockQuantity()) {
                    UIHelper.showError(this,
                        "Cannot add. Total in cart would exceed stock (" + selected.getStockQuantity() + ").");
                    return;
                }
                row[2] = newQty;
                row[4] = newQty * (double) row[3];
                refreshCartTable();
                return;
            }
        }

        double unitPrice = selected.getPrice();
        double subtotal = unitPrice * qty;
        cartItems.add(new Object[]{
                selected.getProductId(),
                selected.getProductName(),
                qty,
                unitPrice,
                subtotal
        });
        refreshCartTable();
        txtQty.setText("1");
    }

    private void removeSelectedItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a cart row to remove.");
            return;
        }
        cartItems.remove(row);
        refreshCartTable();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) return;
        if (UIHelper.confirm(this, "Clear all items from the cart?")) {
            cartItems.clear();
            refreshCartTable();
        }
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        double subTotal = 0;
        for (Object[] row : cartItems) {
            cartModel.addRow(new Object[]{
                    row[0],
                    row[1],
                    String.format("%,.2f", (double) row[3]),
                    row[2],
                    String.format("%,.2f", (double) row[4])
            });
            subTotal += (double) row[4];
        }
        double tax = subTotal * 0.10;
        double total = subTotal + tax;
        lblSubTotal.setText("LKR " + String.format("%,.2f", subTotal));
        lblTax.setText("LKR " + String.format("%,.2f", tax));
        lblTotal.setText("LKR " + String.format("%,.2f", total));
    }

    // =================================================================
    // SAVE ORDER (Main Transaction)
    // =================================================================
    private void saveOrder() {
        if (cmbCustomer.getSelectedItem() == null) {
            UIHelper.showError(this, "Please select a customer.");
            return;
        }
        if (cartItems.isEmpty()) {
            UIHelper.showError(this, "Cart is empty. Add at least one item.");
            return;
        }

        if (!UIHelper.confirm(this, "Save this order and reduce stock?")) return;

        try {
            Customer customer = (Customer) cmbCustomer.getSelectedItem();

            double subTotal = 0;
            for (Object[] row : cartItems) subTotal += (double) row[4];
            double total = subTotal + (subTotal * 0.10);

            SalesOrder order = new SalesOrder();
            order.setCustomerId(customer.getCustomerId());
            order.setOrderDate(new Date());
            order.setTotalAmount(total);
            order.setStatus("Completed");

            int orderId = new SalesOrderDAO().saveOrder(order, cartItems);

            JOptionPane.showMessageDialog(this,
                "Order saved successfully!\nOrder ID: " + orderId +
                "\nTotal: LKR " + String.format("%,.2f", total),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // Reset
            cartItems.clear();
            refreshCartTable();
            loadProducts();   // stock may have changed
            loadOrders();     // new order appears in Order List tab
            tabs.setSelectedIndex(1); // switch to order list

        } catch (SQLException ex) {
            UIHelper.showError(this, "Save order failed: " + ex.getMessage());
        } catch (Exception ex) {
            UIHelper.showError(this, "Unexpected error: " + ex.getMessage());
        }
    }

    // =================================================================
    // DELETE ORDER
    // =================================================================
    private void deleteSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an order to delete.");
            return;
        }
        int orderId = Integer.parseInt(orderModel.getValueAt(row, 0).toString());

        if (!UIHelper.confirm(this,
                "Delete Order #" + orderId + "?\nThis will also delete its items.\nStock will NOT be restored.")) return;

        try {
            new SalesOrderDAO().delete(orderId);
            UIHelper.showInfo(this, "Order deleted.");
            loadOrders();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }
}
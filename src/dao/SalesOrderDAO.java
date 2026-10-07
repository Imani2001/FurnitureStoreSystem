package dao;

import database.DatabaseConnection;
import model.SalesOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderDAO {

    /**
     * Saves a full order: inserts SalesOrder + all OrderDetails,
     * and reduces product stock. Uses a transaction.
     * Returns the new OrderID.
     */
    public int saveOrder(SalesOrder order, List<Object[]> items) throws SQLException {
        Connection c = null;
        try {
            c = DatabaseConnection.getInstance().getConnection();
            c.setAutoCommit(false);

            // Insert order
            String orderSql = "INSERT INTO SalesOrder (CustomerID, OrderDate, TotalAmount, Status) VALUES (?,?,?,?)";
            int orderId;
            try (PreparedStatement ps = c.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getCustomerId());
                ps.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
                ps.setDouble(3, order.getTotalAmount());
                ps.setString(4, order.getStatus());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    orderId = rs.getInt(1);
                }
            }

            // Insert order details + update stock
            String detailSql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice, Subtotal) VALUES (?,?,?,?,?)";
            String stockSql = "UPDATE Product SET StockQuantity = StockQuantity - ? WHERE ProductID=?";
            try (PreparedStatement psD = c.prepareStatement(detailSql);
                 PreparedStatement psS = c.prepareStatement(stockSql)) {
                for (Object[] row : items) {
                    // row = {productId, productName, qty, unitPrice, subtotal}
                    int productId = (int) row[0];
                    int qty = (int) row[2];
                    double unitPrice = (double) row[3];
                    double subtotal = (double) row[4];

                    psD.setInt(1, orderId);
                    psD.setInt(2, productId);
                    psD.setInt(3, qty);
                    psD.setDouble(4, unitPrice);
                    psD.setDouble(5, subtotal);
                    psD.addBatch();

                    psS.setInt(1, qty);
                    psS.setInt(2, productId);
                    psS.addBatch();
                }
                psD.executeBatch();
                psS.executeBatch();
            }

            c.commit();
            return orderId;

        } catch (SQLException e) {
            if (c != null) c.rollback();
            throw e;
        } finally {
            if (c != null) {
                c.setAutoCommit(true);
                c.close();
            }
        }
    }

    public List<SalesOrder> getAll() throws SQLException {
        List<SalesOrder> list = new ArrayList<>();
        String sql = "SELECT so.*, CONCAT(c.FirstName,' ',c.LastName) AS CustomerName " +
                     "FROM SalesOrder so JOIN Customer c ON so.CustomerID=c.CustomerID ORDER BY so.OrderID DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                SalesOrder so = new SalesOrder();
                so.setOrderId(rs.getInt("OrderID"));
                so.setCustomerId(rs.getInt("CustomerID"));
                so.setCustomerName(rs.getString("CustomerName"));
                so.setOrderDate(rs.getDate("OrderDate"));
                so.setTotalAmount(rs.getDouble("TotalAmount"));
                so.setStatus(rs.getString("Status"));
                list.add(so);
            }
        }
        return list;
    }

    public void delete(int orderId) throws SQLException {
        String sql = "DELETE FROM SalesOrder WHERE OrderID=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
}
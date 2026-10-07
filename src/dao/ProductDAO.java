/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.DatabaseConnection;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author imani
 */
public class ProductDAO {
    public List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName FROM Product p JOIN Category c ON p.CategoryID=c.CategoryID ORDER BY p.ProductID DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Product> search(String keyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName FROM Product p JOIN Category c ON p.CategoryID=c.CategoryID " +
                     "WHERE p.ProductName LIKE ? OR c.CategoryName LIKE ? OR p.SupplierName LIKE ?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void add(Product p) throws SQLException {
        String sql = "INSERT INTO Product (ProductName, CategoryID, Price, StockQuantity, SupplierName) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());
            ps.setString(5, p.getSupplierName());
            ps.executeUpdate();
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE Product SET ProductName=?, CategoryID=?, Price=?, StockQuantity=?, SupplierName=? WHERE ProductID=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());
            ps.setString(5, p.getSupplierName());
            ps.setInt(6, p.getProductId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Product WHERE ProductID=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));
        p.setCategoryId(rs.getInt("CategoryID"));
        p.setCategoryName(rs.getString("CategoryName"));
        p.setPrice(rs.getDouble("Price"));
        p.setStockQuantity(rs.getInt("StockQuantity"));
        p.setSupplierName(rs.getString("SupplierName"));
        return p;
    }
}

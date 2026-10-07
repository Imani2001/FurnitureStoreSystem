/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.DatabaseConnection;
import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author imani
 */
public class CustomerDAO {
    public List<Customer> getAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY CustomerID DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Customer> search(String keyword) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE FirstName LIKE ? OR LastName LIKE ? OR Phone LIKE ? OR City LIKE ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k); ps.setString(4, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void add(Customer cu) throws SQLException {
        String sql = "INSERT INTO Customer (FirstName, LastName, Email, Phone, Address, City) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cu.getFirstName());
            ps.setString(2, cu.getLastName());
            ps.setString(3, cu.getEmail());
            ps.setString(4, cu.getPhone());
            ps.setString(5, cu.getAddress());
            ps.setString(6, cu.getCity());
            ps.executeUpdate();
        }
    }

    public void update(Customer cu) throws SQLException {
        String sql = "UPDATE Customer SET FirstName=?, LastName=?, Email=?, Phone=?, Address=?, City=? WHERE CustomerID=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cu.getFirstName());
            ps.setString(2, cu.getLastName());
            ps.setString(3, cu.getEmail());
            ps.setString(4, cu.getPhone());
            ps.setString(5, cu.getAddress());
            ps.setString(6, cu.getCity());
            ps.setInt(7, cu.getCustomerId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Customer WHERE CustomerID=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer cu = new Customer();
        cu.setCustomerId(rs.getInt("CustomerID"));
        cu.setFirstName(rs.getString("FirstName"));
        cu.setLastName(rs.getString("LastName"));
        cu.setEmail(rs.getString("Email"));
        cu.setPhone(rs.getString("Phone"));
        cu.setAddress(rs.getString("Address"));
        cu.setCity(rs.getString("City"));
        return cu;
    }
}

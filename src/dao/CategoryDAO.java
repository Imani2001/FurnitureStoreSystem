/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.DatabaseConnection;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author imani
 */
public class CategoryDAO {
    public List<Category> getAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category ORDER BY CategoryName";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("CategoryID"));
                cat.setCategoryName(rs.getString("CategoryName"));
                cat.setDescription(rs.getString("Description"));
                list.add(cat);
            }
        }
        return list;
    }
}

package com.restaurant.daos.impl;

import com.restaurant.daos.TableDAO;
import com.restaurant.models.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAOImpl implements TableDAO {

    private final Connection conn;

    public TableDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addTable(Table t) {
        String sql = "INSERT INTO tables (number,capacity,x_coord,y_coord,available) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getNumber());
            ps.setInt(2, t.getCapacity());
            ps.setInt(3, t.getX());
            ps.setInt(4, t.getY());
            ps.setBoolean(5, t.isAvailable());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Table getTableById(int id) {
        String sql = "SELECT * FROM tables WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Table> getAllTables() {
        List<Table> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tables")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateTable(Table t) {
        String sql = "UPDATE tables SET number=?, capacity=?, x_coord=?, y_coord=?, available=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getNumber());
            ps.setInt(2, t.getCapacity());
            ps.setInt(3, t.getX());
            ps.setInt(4, t.getY());
            ps.setBoolean(5, t.isAvailable());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTableAvailability(int id, boolean available) {
        String sql = "UPDATE tables SET available=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTable(int id) {
        String sql = "DELETE FROM tables WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Table map(ResultSet rs) throws SQLException {
        return new Table(
                rs.getInt("id"),
                rs.getInt("number"),
                rs.getInt("capacity"),
                rs.getInt("x_coord"),
                rs.getInt("y_coord"),
                rs.getBoolean("available")
        );
    }
}

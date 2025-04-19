package com.restaurant.daos.impl;

import com.restaurant.daos.PaymentDAO;
import com.restaurant.models.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    private final Connection conn;
    public PaymentDAOImpl(Connection c){ this.conn=c; }

    @Override
    public void addPayment(Payment p) {
        String sql="INSERT INTO payments (order_id, amount, method, status, ts) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,p.getOrderId());
            ps.setDouble(2,p.getAmount());
            ps.setString(3,p.getMethod());
            ps.setString(4,p.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(p.getTimestamp()));
            ps.executeUpdate();
        } catch(SQLException e){e.printStackTrace();}
    }

    @Override
    public Payment getPaymentById(int id) {
        String sql="SELECT * FROM payments WHERE id=?";
        try (PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()) return map(rs);
        }catch(SQLException e){e.printStackTrace();}
        return null;
    }

    @Override
    public List<Payment> getPaymentsByOrder(int orderId) {
        List<Payment> list=new ArrayList<>();
        String sql="SELECT * FROM payments WHERE order_id=?";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,orderId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) list.add(map(rs));
        }catch(SQLException e){e.printStackTrace();}
        return list;
    }

    @Override
    public void updatePaymentStatus(int id,String status){
        String sql="UPDATE payments SET status=? WHERE id=?";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,status);
            ps.setInt(2,id);
            ps.executeUpdate();
        }catch(SQLException e){e.printStackTrace();}
    }

    @Override
    public void deletePayment(int id){
        String sql="DELETE FROM payments WHERE id=?";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        }catch(SQLException e){e.printStackTrace();}
    }

    private Payment map(ResultSet rs)throws SQLException{
        return new Payment(
                rs.getInt("id"),
                rs.getInt("order_id"),
                rs.getDouble("amount"),
                rs.getString("method"),
                rs.getString("status"),
                rs.getTimestamp("ts").toLocalDateTime()
        );
    }
}
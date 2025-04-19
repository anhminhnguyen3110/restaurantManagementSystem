package com.restaurant.daos.impl;

import com.restaurant.constants.BookingDuration;
import com.restaurant.daos.BookingDAO;
import com.restaurant.models.Booking;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    private final Connection conn;

    public BookingDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_name, phone_number, start_time, duration_minutes, table_id) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, booking.getCustomerName());
            ps.setString(2, booking.getPhoneNumber());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStart()));
            ps.setInt(4, booking.getDuration().getMinutes());
            ps.setInt(5, booking.getTableId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Booking getBookingByPhone(String phone) {
        String sql = "SELECT * FROM bookings WHERE phone_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Booking getBookingById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
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
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Booking> getBookingsInRange(LocalDateTime from, LocalDateTime to) {
        List<Booking> list = new ArrayList<>();
        String sql = """
            SELECT * FROM bookings
             WHERE start_time <= ?
               AND TIMESTAMPADD(MINUTE, duration_minutes, start_time) >= ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(to));
            ps.setTimestamp(2, Timestamp.valueOf(from));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET customer_name=?, phone_number=?, start_time=?, duration_minutes=?, table_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, booking.getCustomerName());
            ps.setString(2, booking.getPhoneNumber());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStart()));
            ps.setInt(4, booking.getDuration().getMinutes());
            ps.setInt(5, booking.getTableId());
            ps.setInt(6, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Booking map(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getString("phone_number"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                BookingDuration.fromMinutes(rs.getInt("duration_minutes")),
                rs.getInt("table_id")
        );
    }
}
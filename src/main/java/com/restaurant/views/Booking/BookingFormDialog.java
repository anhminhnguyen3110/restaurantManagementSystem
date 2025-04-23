package com.restaurant.views.Booking;

import com.restaurant.controllers.BookingController;
import com.restaurant.daos.CustomerDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Injector;
import com.restaurant.models.Booking;
import com.restaurant.models.Customer;
import com.restaurant.models.RestaurantTable;
import com.restaurant.constants.BookingDuration;
import com.restaurant.constants.BookingStatus;
import com.restaurant.dtos.booking.GetBookingsDto;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class BookingFormDialog extends JDialog {
    private final JTextField phoneField = new JTextField(15);
    private final JXDatePicker dpStart = new JXDatePicker();
    private final JComboBox<String> cbStartTime;
    private final JComboBox<BookingDuration> durationCombo = new JComboBox<>(BookingDuration.values());
    private final JComboBox<RestaurantTable> tableCombo = new JComboBox<>();
    private final JComboBox<BookingStatus> statusCombo = new JComboBox<>(BookingStatus.values());
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");
    private final BookingController controller;
    private final Booking booking;
    private final Runnable onSaved;

    public BookingFormDialog(Frame owner, BookingController controller, Booking booking, Runnable onSaved) {
        super(owner, booking == null ? "New Booking" : "Edit Booking", true);
        this.controller = controller;
        this.booking = booking;
        this.onSaved = onSaved;

        String[] times = new String[48];
        for (int i = 0; i < 48; i++) {
            int h = i / 2;
            int m = (i % 2) * 30;
            times[i] = String.format("%02d:%02d", h, m);
        }
        cbStartTime = new JComboBox<>(times);

        RestaurantTableDAO tableDao = Injector.getInstance().getInstance(RestaurantTableDAO.class);
        List<RestaurantTable> tables = tableDao.findAll();
        tables.forEach(tableCombo::addItem);
        tableCombo.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : ((RestaurantTable)value).toString());
                return this;
            }
        });

        if (booking != null) {
            phoneField.setText(booking.getCustomer().getPhoneNumber());
            LocalDateTime start = booking.getStart();
            dpStart.setDate(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
            String ts = String.format("%02d:%02d", start.getHour(), start.getMinute());
            cbStartTime.setSelectedItem(ts);
            durationCombo.setSelectedItem(booking.getDuration());
            tableCombo.setSelectedItem(booking.getTable());
            statusCombo.setSelectedItem(booking.getStatus());
        }

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(6,2,5,5));
        form.add(new JLabel("Customer Phone:")); form.add(phoneField);
        form.add(new JLabel("Start Date:"));      form.add(dpStart);
        form.add(new JLabel("Start Time:"));      form.add(cbStartTime);
        form.add(new JLabel("Duration:"));        form.add(durationCombo);
        form.add(new JLabel("Table:"));           form.add(tableCombo);
        form.add(new JLabel("Status:"));          form.add(statusCombo);

        JPanel buttons = new JPanel();
        buttons.add(btnSave); buttons.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            String phone = phoneField.getText().trim();
            LocalDate date = dpStart.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String[] parts = ((String)cbStartTime.getSelectedItem()).split(":");
            LocalTime time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            LocalDateTime start = LocalDateTime.of(date, time);
            BookingDuration duration = (BookingDuration) durationCombo.getSelectedItem();
            LocalDateTime end = start.plusMinutes(duration.getMinutes());
            RestaurantTable table = (RestaurantTable) tableCombo.getSelectedItem();
            BookingStatus status = (BookingStatus) statusCombo.getSelectedItem();

            CustomerDAO custDao = Injector.getInstance().getInstance(CustomerDAO.class);
            Customer c = custDao.findByPhoneNumber(phone);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "No customer found with phone " + phone, "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            GetBookingsDto dto = new GetBookingsDto();
            dto.setTableNumber(table.getNumber());
            dto.setFrom(start);
            dto.setTo(end);
            dto.setPage(0);
            dto.setSize(Integer.MAX_VALUE);
            List<Booking> conflicts = controller.findBookings(dto);
            for (Booking b : conflicts) {
                if (booking == null || b.getId() != booking.getId()) {
                    JOptionPane.showMessageDialog(this, "Table " + table.getNumber() + " is already booked during selected time", "Validation", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Booking b = booking == null ? new Booking() : controller.getBooking(booking.getId());
            b.setCustomer(c);
            b.setStart(start);
            b.setDuration(duration);
            b.setTable(table);
            b.setStatus(status);

            if (booking == null) controller.createBooking(b); else controller.updateBooking(b);

            onSaved.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.restaurant.views.booking;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.BookingController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.Booking;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import com.restaurant.utils.validators.BookingInputValidator;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class BookingFormDialog extends JDialog {
    private final JComboBox<Restaurant> restaurantCombo = new JComboBox<>();
    private final JXDatePicker datePicker = new JXDatePicker();
    private final JComboBox<BookingTimeSlot> startCombo = new JComboBox<>(BookingTimeSlot.values());
    private final JComboBox<BookingTimeSlot> endCombo = new JComboBox<>(BookingTimeSlot.values());
    private final JComboBox<RestaurantTable> tableCombo = new JComboBox<>();
    private final JTextField nameField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JComboBox<BookingStatus> statusCombo = new JComboBox<>(BookingStatus.values());
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private final BookingController bookingController;
    private final RestaurantTableController tableController;
    private final Booking existing;
    private final Runnable onSaved;

    public BookingFormDialog(
            Frame owner,
            Booking existing,
            Runnable onSaved
    ) {
        super(owner, existing == null ? "New Booking" : "Edit Booking", true);

        this.bookingController = Injector.getInstance().getInstance(BookingController.class);
        RestaurantController restaurantController = Injector.getInstance()
                .getInstance(RestaurantController.class);
        this.tableController = Injector.getInstance()
                .getInstance(RestaurantTableController.class);
        this.existing = existing;
        this.onSaved = onSaved;

        List<Restaurant> restaurants = restaurantController.findAllRestaurants();
        restaurants.forEach(restaurantCombo::addItem);
        restaurantCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Restaurant) {
                    setText(((Restaurant) value).getName());
                }
                return this;
            }
        });

        tableCombo.setEnabled(false);
        restaurantCombo.addActionListener(e -> refreshTables());
        datePicker.addActionListener(e -> refreshTables());
        startCombo.addActionListener(e -> refreshTables());
        endCombo.addActionListener(e -> refreshTables());

        if (existing != null) {
            nameField.setText(existing.getCustomer().getName());
            phoneField.setText(existing.getCustomer().getPhoneNumber());
            emailField.setText(existing.getCustomer().getEmail());
            datePicker.setDate(Date.from(
                    existing.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
            startCombo.setSelectedItem(existing.getStartTime());
            endCombo.setSelectedItem(existing.getEndTime());
            restaurantCombo.setSelectedItem(existing.getTable().getRestaurant());
            statusCombo.setSelectedItem(existing.getStatus());
            SwingUtilities.invokeLater(
                    () -> tableCombo.setSelectedItem(existing.getTable())
            );
        }

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel f = new JPanel(new GridLayout(9, 2, 5, 5));
        f.add(new JLabel("Restaurant:"));
        f.add(restaurantCombo);
        f.add(new JLabel("Date:"));
        f.add(datePicker);
        f.add(new JLabel("Start:"));
        f.add(startCombo);
        f.add(new JLabel("End:"));
        f.add(endCombo);
        f.add(new JLabel("Table:"));
        f.add(tableCombo);
        f.add(new JLabel("Name:"));
        f.add(nameField);
        f.add(new JLabel("Phone:"));
        if (existing == null) {
            f.add(phoneField);
        } else {
            phoneField.setEnabled(false);
        }
        f.add(phoneField);
        f.add(new JLabel("Email:"));
        f.add(emailField);
        if (existing != null) {
            f.add(new JLabel("Status:"));
            f.add(statusCombo);
        }

        JPanel b = new JPanel();
        b.add(btnSave);
        b.add(btnCancel);
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(f, BorderLayout.CENTER);
        getContentPane().add(b, BorderLayout.SOUTH);
    }

    private void refreshTables() {
        Restaurant r = (Restaurant) restaurantCombo.getSelectedItem();
        Date d = datePicker.getDate();
        BookingTimeSlot s = (BookingTimeSlot) startCombo.getSelectedItem();
        BookingTimeSlot e = (BookingTimeSlot) endCombo.getSelectedItem();
        boolean ok = r != null && d != null && s != null && e != null && e.ordinal() > s.ordinal();
        tableCombo.setEnabled(ok);
        if (!ok) {
            tableCombo.removeAllItems();
            return;
        }
        GetRestaurantTableForBookingDto dto = new GetRestaurantTableForBookingDto();
        dto.setRestaurantId(r.getId());
        dto.setDate(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dto.setStartTime(s);
        dto.setEndTime(e);
        List<RestaurantTable> ts = tableController.findTablesForBooking(dto);
        tableCombo.removeAllItems();
        ts.forEach(tableCombo::addItem);
    }

    private void onSave() {
        Date picked = datePicker.getDate();
        if (picked == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "• Date is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LocalDate date = picked.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String name = nameField.getText().trim(),
                phone = phoneField.getText().trim(),
                email = emailField.getText().trim();
        BookingTimeSlot s = (BookingTimeSlot) startCombo.getSelectedItem(),
                e = (BookingTimeSlot) endCombo.getSelectedItem();
        RestaurantTable t = (RestaurantTable) tableCombo.getSelectedItem();
        BookingStatus st = (BookingStatus) statusCombo.getSelectedItem();

        if (existing == null) {
            CreateBookingDto dto = new CreateBookingDto();
            dto.setCustomerName(name);
            dto.setCustomerPhoneNumber(phone);
            dto.setCustomerEmail(email);
            dto.setDate(date);
            dto.setStartTime(s);
            dto.setEndTime(e);
            dto.setTableId(t != null ? t.getId() : 0);

            List<String> errors = BookingInputValidator.validate(dto);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        String.join("\n", errors),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            bookingController.createBooking(dto);

        } else {
            UpdateBookingDto dto = new UpdateBookingDto();
            dto.setId(existing.getId());
            dto.setCustomerName(name);
            dto.setCustomerPhoneNumber(phone);
            dto.setCustomerEmail(email);
            dto.setDate(date);
            dto.setStartTime(s);
            dto.setEndTime(e);
            dto.setTableId(t != null ? t.getId() : 0);
            dto.setStatus(st);

            List<String> errors = BookingInputValidator.validate(dto);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        String.join("\n", errors),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            bookingController.updateBooking(dto);
        }

        onSaved.run();
        dispose();
    }
}
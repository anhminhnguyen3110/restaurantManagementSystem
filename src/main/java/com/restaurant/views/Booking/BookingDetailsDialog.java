package com.restaurant.views.Booking;

import com.restaurant.models.Booking;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.util.Date;

public class BookingDetailsDialog extends JDialog {
    public BookingDetailsDialog(Window owner, Booking b) {
        super(owner, "Booking #" + b.getId(), ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;

        content.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(String.valueOf(b.getId())), gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(b.getCustomer().getName()), gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Restaurant:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(b.getTable().toString()), gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Table #:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(String.valueOf(b.getTable().getNumber())), gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Seats:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(String.valueOf(b.getTable().getCapacity())), gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        JXDatePicker dpStart = new JXDatePicker();
        dpStart.setDate(Date.from(b.getStart().atZone(ZoneId.systemDefault()).toInstant()));
        dpStart.setEnabled(false);
        content.add(dpStart, gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JSpinner spStart = new JSpinner(new SpinnerDateModel());
        spStart.setEditor(new JSpinner.DateEditor(spStart, "HH:mm"));
        spStart.setValue(Date.from(b.getStart().atZone(ZoneId.systemDefault()).toInstant()));
        spStart.setEnabled(false);
        content.add(spStart, gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        JXDatePicker dpEnd = new JXDatePicker();
        dpEnd.setDate(Date.from(b.getEnd().atZone(ZoneId.systemDefault()).toInstant()));
        dpEnd.setEnabled(false);
        content.add(dpEnd, gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JSpinner spEnd = new JSpinner(new SpinnerDateModel());
        spEnd.setEditor(new JSpinner.DateEditor(spEnd, "HH:mm"));
        spEnd.setValue(Date.from(b.getEnd().atZone(ZoneId.systemDefault()).toInstant()));
        spEnd.setEnabled(false);
        content.add(spEnd, gbc);

        gbc.gridx = 0; gbc.gridy++; content.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; content.add(new JLabel(b.getStatus().toString()), gbc);

        add(new JScrollPane(content), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel p = new JPanel();
        p.add(btnClose);
        add(p, BorderLayout.SOUTH);

        setSize(400, 450);
        setLocationRelativeTo(owner);
    }
}

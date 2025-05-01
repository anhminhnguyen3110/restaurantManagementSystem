package com.restaurant.views.restaurant;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.models.Restaurant;
import com.restaurant.utils.validators.RestaurantInputValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurantFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtAddress = new JTextField(15);
    private final JSpinner spnMaxX = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner spnMaxY = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JComboBox<RestaurantStatus> cmbStatus;
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private final RestaurantController controller;
    private final Restaurant existing;
    private final Runnable onSaved;

    public RestaurantFormDialog(
            Frame owner,
            Restaurant existing,
            Runnable onSaved
    ) {
        super(owner,
                existing == null ? "New Restaurant" : "Edit Restaurant",
                true);
        this.controller = Injector.getInstance()
                .getInstance(RestaurantController.class);
        this.existing = existing;
        this.onSaved = onSaved;

        cmbStatus = new JComboBox<>(RestaurantStatus.values());

        buildUI();
        pack();
        setLocationRelativeTo(owner);

        if (existing != null) {
            txtName.setText(existing.getName());
            txtAddress.setText(existing.getAddress());
            cmbStatus.setSelectedItem(existing.getStatus());
            spnMaxX.setValue(existing.getMaxX());
            spnMaxY.setValue(existing.getMaxY());
            spnMaxX.setEnabled(false);
            spnMaxY.setEnabled(false);
        }
    }

    private void buildUI() {
        JPanel f = new JPanel(new GridLayout(5, 2, 5, 5));
        f.add(new JLabel("Name:"));
        f.add(txtName);
        f.add(new JLabel("Address:"));
        f.add(txtAddress);
        if (existing != null) {
            f.add(new JLabel("Status:"));
            f.add(cmbStatus);
        }
        f.add(new JLabel("Map Width (X):"));
        f.add(spnMaxX);
        f.add(new JLabel("Map Height (Y):"));
        f.add(spnMaxY);

        JPanel b = new JPanel();
        b.add(btnSave);
        b.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(f, BorderLayout.CENTER);
        getContentPane().add(b, BorderLayout.SOUTH);
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String addr = txtAddress.getText().trim();
        int maxX = (int) spnMaxX.getValue();
        int maxY = (int) spnMaxY.getValue();

        if (existing == null) {
            CreateRestaurantDto dto = new CreateRestaurantDto();
            dto.setName(name);
            dto.setAddress(addr);
            dto.setMaxX(maxX);
            dto.setMaxY(maxY);

            List<String> errs = RestaurantInputValidator.validate(dto);
            if (!errs.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", errs),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.createRestaurant(dto);
        } else {
            UpdateRestaurantDto dto = new UpdateRestaurantDto();
            dto.setId(existing.getId());
            dto.setName(name);
            dto.setAddress(addr);
            dto.setMaxX(existing.getMaxX());
            dto.setMaxY(existing.getMaxY());
            if (existing.getStatus() != null) {
                dto.setStatus(existing.getStatus());
            }

            List<String> errs = RestaurantInputValidator.validate(dto);
            if (!errs.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        String.join("\n", errs),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.updateRestaurant(dto);
        }

        onSaved.run();
        dispose();
    }
}
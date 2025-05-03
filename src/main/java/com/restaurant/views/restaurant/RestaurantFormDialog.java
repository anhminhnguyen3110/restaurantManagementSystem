package com.restaurant.views.restaurant;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.models.Restaurant;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import java.awt.*;

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

            Validator<CreateRestaurantDto, UpdateRestaurantDto> v =
                    ValidatorFactory.getCreateValidator(CreateRestaurantDto.class);
            if (!v.triggerCreateErrors(dto)) return;

            controller.createRestaurant(dto);
        } else {
            UpdateRestaurantDto dto = new UpdateRestaurantDto();
            dto.setId(existing.getId());
            dto.setName(name);
            dto.setAddress(addr);
            dto.setMaxX(maxX == 0 ? existing.getMaxX() : maxX);
            dto.setMaxY(maxY == 0 ? existing.getMaxY() : maxY);
            dto.setStatus(existing.getStatus());

            Validator<CreateRestaurantDto, UpdateRestaurantDto> v =
                    ValidatorFactory.getUpdateValidator(UpdateRestaurantDto.class);
            if (!v.triggerUpdateErrors(dto)) return;

            controller.updateRestaurant(dto);
        }

        onSaved.run();
        dispose();
    }
}
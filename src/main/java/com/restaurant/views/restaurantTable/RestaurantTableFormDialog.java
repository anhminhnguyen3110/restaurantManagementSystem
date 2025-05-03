package com.restaurant.views.restaurantTable;

import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import com.restaurant.models.RestaurantTable;
import com.restaurant.utils.validators.RestaurantTableInputValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurantTableFormDialog extends JDialog {
    private final JSpinner spnNumber = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JSpinner spnCapacity = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JSpinner spnStartX = new JSpinner();
    private final JSpinner spnStartY = new JSpinner();
    private final JSpinner spnEndX = new JSpinner();
    private final JSpinner spnEndY = new JSpinner();
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private final RestaurantTableController controller;
    private final RestaurantTable existing;
    private final int restaurantId;
    private final Runnable onSaved;

    public RestaurantTableFormDialog(Frame owner, RestaurantTable existing, int restaurantId, Runnable onSaved) {
        this(owner, existing, restaurantId, existing.getStartX(), existing.getStartY(), existing.getEndX(), existing.getEndY(), onSaved);
    }

    public RestaurantTableFormDialog(Frame owner, RestaurantTable existing, int restaurantId, int startX, int startY, int endX, int endY, Runnable onSaved) {
        super(owner, existing == null ? "New Table" : "Edit Table", true);
        this.controller = Injector.getInstance().getInstance(RestaurantTableController.class);
        this.existing = existing;
        this.restaurantId = restaurantId;
        this.onSaved = onSaved;

        spnNumber.setEditor(new JSpinner.NumberEditor(spnNumber, "'#'0"));
        if (existing == null) {
            GetRestaurantTableDto getDto = new GetRestaurantTableDto();
            getDto.setRestaurantId(restaurantId);
            List<RestaurantTable> tables = controller.findTables(getDto);
            int max = tables.stream().mapToInt(RestaurantTable::getNumber).max().orElse(0);
            spnNumber.setValue(max + 1);
        } else {
            spnNumber.setValue(existing.getNumber());
        }
        spnNumber.setEnabled(false);

        spnCapacity.setModel(new SpinnerNumberModel(existing != null ? existing.getCapacity() : 1, 1, 20, 1));
        spnStartX.setModel(new SpinnerNumberModel(startX, 0, 9, 1));
        spnStartY.setModel(new SpinnerNumberModel(startY, 0, 9, 1));
        spnEndX.setModel(new SpinnerNumberModel(endX, 0, 9, 1));
        spnEndY.setModel(new SpinnerNumberModel(endY, 0, 9, 1));

        buildUI();
        pack();
        setLocationRelativeTo(owner);

        if (existing == null) {
            spnStartX.setEnabled(false);
            spnStartY.setEnabled(false);
            spnEndX.setEnabled(false);
            spnEndY.setEnabled(false);
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.add(new JLabel("Table #:"));
        form.add(spnNumber);
        form.add(new JLabel("Capacity:"));
        form.add(spnCapacity);
        form.add(new JLabel("Start X:"));
        form.add(spnStartX);
        form.add(new JLabel("Start Y:"));
        form.add(spnStartY);
        form.add(new JLabel("End X:"));
        form.add(spnEndX);
        form.add(new JLabel("End Y:"));
        form.add(spnEndY);

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave() {
        int num = (int) spnNumber.getValue();
        int cap = (int) spnCapacity.getValue();
        int sx = (int) spnStartX.getValue();
        int sy = (int) spnStartY.getValue();
        int ex = (int) spnEndX.getValue();
        int ey = (int) spnEndY.getValue();

        if (existing == null) {
            CreateRestaurantTableDto dto = new CreateRestaurantTableDto();
            dto.setNumber(num);
            dto.setCapacity(cap);
            dto.setRestaurantId(restaurantId);
            dto.setStartX(sx);
            dto.setStartY(sy);
            dto.setEndX(ex);
            dto.setEndY(ey);
            List<String> errors = RestaurantTableInputValidator.validate(dto);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.createTable(dto);
        } else {
            UpdateRestaurantTableDto dto = new UpdateRestaurantTableDto();
            dto.setId(existing.getId());
            dto.setNumber(num);
            dto.setCapacity(cap);
            dto.setRestaurantId(restaurantId);
            dto.setStartX(sx);
            dto.setStartY(sy);
            dto.setEndX(ex);
            dto.setEndY(ey);
            List<String> errors = RestaurantTableInputValidator.validate(dto);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.updateTable(dto);
        }

        onSaved.run();
        dispose();
    }
}

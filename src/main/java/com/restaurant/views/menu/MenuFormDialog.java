package com.restaurant.views.menu;

import com.restaurant.controllers.MenuController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import com.restaurant.models.Menu;
import com.restaurant.models.Restaurant;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import java.awt.*;

public class MenuFormDialog extends JDialog {
    private final JTextField nameField = new JTextField(15);
    private final JTextField descField = new JTextField(15);
    private final JComboBox<Restaurant> restaurantCombo = new JComboBox<>();
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private final MenuController menuController;
    private final Menu existing;
    private final Runnable onSaved;

    public MenuFormDialog(Frame owner, Menu existing, Runnable onSaved) {
        super(owner, existing == null ? "New Menu" : "Edit Menu", true);
        this.menuController = Injector.getInstance().getInstance(MenuController.class);
        RestaurantController restaurantController = Injector.getInstance().getInstance(RestaurantController.class);
        this.existing = existing;
        this.onSaved = onSaved;

        for (Restaurant r : restaurantController.findAllRestaurants()) {
            restaurantCombo.addItem(r);
        }
        restaurantCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Restaurant ? ((Restaurant) value).getName() : "");
                return this;
            }
        });

        if (existing != null) {
            nameField.setText(existing.getName());
            descField.setText(existing.getDescription());
            restaurantCombo.setSelectedItem(existing.getRestaurant());
        }

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel f = new JPanel(new GridLayout(3, 2, 5, 5));
        f.add(new JLabel("Name:"));
        f.add(nameField);
        f.add(new JLabel("Description:"));
        f.add(descField);
        f.add(new JLabel("Restaurant:"));
        f.add(restaurantCombo);

        JPanel b = new JPanel();
        b.add(btnSave);
        b.add(btnCancel);
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        getContentPane().add(f, BorderLayout.CENTER);
        getContentPane().add(b, BorderLayout.SOUTH);
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        Restaurant r = (Restaurant) restaurantCombo.getSelectedItem();

        if (existing == null) {
            CreateMenuDto dto = new CreateMenuDto();
            dto.setName(name);
            dto.setDescription(desc);
            dto.setRestaurantId(r != null ? r.getId() : 0);

            Validator<CreateMenuDto, UpdateMenuDto> v =
                    ValidatorFactory.getCreateValidator(CreateMenuDto.class);
            if (!v.triggerCreateErrors(dto)) return;
            menuController.createMenu(dto);
        } else {
            UpdateMenuDto dto = new UpdateMenuDto();
            dto.setId(existing.getId());
            dto.setName(name);
            dto.setDescription(desc);
            dto.setRestaurantId(r != null ? r.getId() : 0);

            Validator<CreateMenuDto, UpdateMenuDto> v =
                    ValidatorFactory.getUpdateValidator(UpdateMenuDto.class);
            if (!v.triggerUpdateErrors(dto)) return;
            menuController.updateMenu(dto);
        }

        onSaved.run();
        dispose();
    }

}

package com.restaurant.views.menuItem;

import com.restaurant.controllers.MenuController;
import com.restaurant.controllers.MenuItemController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Restaurant;
import com.restaurant.validators.Validator;
import com.restaurant.validators.ValidatorFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuItemFormDialog extends JDialog {
    private final MenuItemController menuItemController;
    private final RestaurantController restaurantController;
    private final MenuController menuController;
    private final MenuItem existing;
    private final Runnable onSaved;
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JComboBox<Menu> cmbMenu = new JComboBox<>();
    private final JTextField txtName = new JTextField(15);
    private final JTextArea txtDescription = new JTextArea(3, 15);
    private final JTextField txtPrice = new JTextField(10);
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    public MenuItemFormDialog(Frame owner, MenuItem existing, Runnable onSaved) {
        super(owner, existing == null ? "New Menu Item" : "Edit Menu Item", true);
        this.menuItemController = Injector.getInstance().getInstance(MenuItemController.class);
        this.restaurantController = Injector.getInstance().getInstance(RestaurantController.class);
        this.menuController = Injector.getInstance().getInstance(MenuController.class);
        this.existing = existing;
        this.onSaved = onSaved;
        initUI();
        setSize(400, 350);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Restaurant:"), c);
        c.gridx = 1;
        if (existing == null) {
            for (Restaurant r : restaurantController.findAllRestaurants()) cmbRestaurant.addItem(r);
            cmbRestaurant.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foc) {
                    super.getListCellRendererComponent(list, value, index, sel, foc);
                    setText(value instanceof Restaurant ? ((Restaurant) value).getName() : "");
                    return this;
                }
            });
            p.add(cmbRestaurant, c);
        } else {
            p.add(new JLabel(existing.getMenu().getRestaurant().getName()), c);
        }

        c.gridx = 0;
        c.gridy = 1;
        p.add(new JLabel("Menu:"), c);
        c.gridx = 1;
        if (existing == null) {
            cmbMenu.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foc) {
                    super.getListCellRendererComponent(list, value, index, sel, foc);
                    setText(value instanceof Menu ? ((Menu) value).getName() : "");
                    return this;
                }
            });
            p.add(cmbMenu, c);
        } else {
            p.add(new JLabel(existing.getMenu().getName()), c);
        }

        c.gridx = 0;
        c.gridy = 2;
        p.add(new JLabel("Name:"), c);
        c.gridx = 1;
        p.add(txtName, c);

        c.gridx = 0;
        c.gridy = 3;
        p.add(new JLabel("Description:"), c);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        c.gridx = 1;
        p.add(descScroll, c);

        c.gridx = 0;
        c.gridy = 4;
        p.add(new JLabel("Price:"), c);
        c.gridx = 1;
        p.add(txtPrice, c);

        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);

        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        if (existing == null) {
            cmbRestaurant.addActionListener(e -> {
                cmbMenu.removeAllItems();
                Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
                if (r != null) {
                    GetMenuDto mdto = new GetMenuDto();
                    mdto.setRestaurantName(r.getName());
                    menuController.findMenus(mdto).forEach(cmbMenu::addItem);
                }
            });
            cmbRestaurant.setSelectedIndex(0);
        }

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        if (existing != null) {
            txtName.setText(existing.getName());
            txtDescription.setText(existing.getDescription());
            txtPrice.setText(String.valueOf(existing.getPrice()));
        }
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String desc = txtDescription.getText().trim();
        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
        } catch (Exception ex) {
            price = 0;
        }

        if (existing == null) {
            Menu m = (Menu) cmbMenu.getSelectedItem();
            CreateMenuItemDto dto = new CreateMenuItemDto();
            dto.setName(name);
            dto.setDescription(desc);
            dto.setPrice(price);
            dto.setMenuId(m != null ? m.getId() : 0);

            Validator<CreateMenuItemDto, UpdateMenuItemDto> v =
                    ValidatorFactory.getCreateValidator(CreateMenuItemDto.class);
            if (!v.triggerCreateErrors(dto)) return;

            menuItemController.createMenuItem(dto);
        } else {
            UpdateMenuItemDto dto = new UpdateMenuItemDto();
            dto.setId(existing.getId());
            dto.setName(name);
            dto.setDescription(desc);
            dto.setPrice(price);
            dto.setMenuId(existing.getMenu().getId());

            Validator<CreateMenuItemDto, UpdateMenuItemDto> v =
                    ValidatorFactory.getUpdateValidator(UpdateMenuItemDto.class);
            if (!v.triggerUpdateErrors(dto)) return;

            menuItemController.updateMenuItem(dto);
        }

        onSaved.run();
        dispose();
    }
}

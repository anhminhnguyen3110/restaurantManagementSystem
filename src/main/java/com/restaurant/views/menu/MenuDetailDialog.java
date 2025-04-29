package com.restaurant.views.menu;

import com.restaurant.controllers.MenuController;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;
import com.restaurant.di.Injector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuDetailDialog extends JDialog {
    private final MenuController menuController;
    private final int menuId;

    public MenuDetailDialog(Frame owner, int menuId) {
        super(owner, "Menu Details", true);
        this.menuController = Injector.getInstance().getInstance(MenuController.class);
        this.menuId = menuId;
        initUI();
    }

    private void initUI() {
        Menu menu = menuController.getMenu(menuId);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        JPanel header = new JPanel(new BorderLayout(5, 5));
        JLabel lblName = new JLabel(menu.getName());
        lblName.setFont(lblName.getFont().deriveFont(Font.BOLD, 18f));
        header.add(lblName, BorderLayout.NORTH);

        JLabel lblRestaurant = new JLabel("Restaurant: " + menu.getRestaurant().getName());
        lblRestaurant.setFont(lblRestaurant.getFont().deriveFont(Font.ITALIC, 12f));
        header.add(lblRestaurant, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));

        String[] cols = {"Name", "Description", "Price", "Ordered Count"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<MenuItem> items = menu.getItems();
        for (MenuItem it : items) {
            model.addRow(new Object[]{
                    it.getName(),
                    it.getDescription(),
                    String.format("$%.2f", it.getPrice()),
                    it.getTotalOrderedCount()
            });
        }

        JTable tblItems = new JTable(model);
        tblItems.setFillsViewportHeight(true);
        tblItems.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(tblItems);
        tableScroll.setBorder(new TitledBorder("Menu Items (" + items.size() + ")"));
        center.add(tableScroll, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);
        content.add(footer, BorderLayout.SOUTH);

        setSize(550, 650);
        setLocationRelativeTo(getOwner());
    }
}
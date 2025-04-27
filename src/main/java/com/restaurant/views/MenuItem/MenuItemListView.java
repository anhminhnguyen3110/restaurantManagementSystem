package com.restaurant.views.MenuItem;

import com.restaurant.controllers.MenuItemController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.controllers.MenuController;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Restaurant;
import com.restaurant.di.Injector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuItemListView extends JPanel {
    private static final String[] COLUMNS = {
            "ID", "Name", "Menu", "Restaurant", "Price", "Ordered"
    };
    private final MenuItemController menuItemController;
    private final RestaurantController restaurantController;
    private final MenuController menuController;
    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JComboBox<Menu> cmbMenu = new JComboBox<>();
    private final JTextField txtName = new JTextField(8);
    private final JTextField txtMinPrice = new JTextField(6);
    private final JTextField txtMaxPrice = new JTextField(6);
    private final JButton btnFilter = new JButton("Filter");
    private final JButton btnReset  = new JButton("Reset");
    private final JButton btnAdd    = new JButton("Add");
    private final JButton btnDel    = new JButton("Delete");
    private final JButton btnPrev   = new JButton("Previous");
    private final JButton btnNext   = new JButton("Next");
    private final GetMenuItemsDto currentDto = new GetMenuItemsDto();
    private String sortBy;
    private String sortDir = "asc";

    public MenuItemListView() {
        menuItemController     = Injector.getInstance().getInstance(MenuItemController.class);
        restaurantController   = Injector.getInstance().getInstance(RestaurantController.class);
        menuController         = Injector.getInstance().getInstance(MenuController.class);

        setLayout(new BorderLayout(10,10));

        JPanel filters = new JPanel();
        filters.add(new JLabel("Restaurant:"));
        cmbRestaurant.addItem(null);
        restaurantController.findAllRestaurants().forEach(cmbRestaurant::addItem);
        cmbRestaurant.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean foc
            ){
                super.getListCellRendererComponent(list, value, index, sel, foc);
                setText(value instanceof Restaurant
                        ? ((Restaurant) value).getName()
                        : ""
                );
                return this;
            }
        });
        filters.add(cmbRestaurant);

        filters.add(new JLabel("Menu:"));
        cmbMenu.addItem(null);
        cmbMenu.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean foc
            ){
                super.getListCellRendererComponent(list, value, index, sel, foc);
                setText(value instanceof Menu
                        ? ((Menu) value).getName()
                        : ""
                );
                return this;
            }
        });
        filters.add(cmbMenu);

        filters.add(new JLabel("Name:"));     filters.add(txtName);
        filters.add(new JLabel("Min Price:"));filters.add(txtMinPrice);
        filters.add(new JLabel("Max Price:"));filters.add(txtMaxPrice);

        filters.add(btnFilter);
        filters.add(btnReset);
        filters.add(btnAdd);
        filters.add(btnDel);

        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c
            ){
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel
                                ? tbl.getSelectionBackground()
                                : (r % 2 == 0
                                ? Color.WHITE
                                : new Color(245,245,245)
                        )
                );
                return this;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        cmbRestaurant.addActionListener(e -> loadMenus());
        btnFilter.addActionListener(e -> applyFilters());
        btnReset.addActionListener(e -> { resetFilters(); loadData(); });
        btnAdd.addActionListener(e -> openForm(null));
        btnDel.addActionListener(e -> deleteSelected());
        btnPrev.addActionListener(e -> {
            if (currentDto.getPage() > 0) {
                currentDto.setPage(currentDto.getPage() - 1);
                loadData();
            }
        });
        btnNext.addActionListener(e -> {
            currentDto.setPage(currentDto.getPage() + 1);
            loadData();
        });

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String key = null;
                if (col == 4) key = "price";
                else if (col == 5) key = "ordered";
                if (key != null) {
                    if (key.equals(sortBy)) sortDir = sortDir.equals("asc") ? "desc" : "asc";
                    else { sortBy = key; sortDir = "asc"; }
                    loadData();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) model.getValueAt(
                                table.convertRowIndexToModel(row), 0
                        );
                        MenuItem item = menuItemController.getMenuItem(id);
                        openForm(item);
                    }
                }
            }
        });

        resetFilters();
        loadData();
    }

    private void loadMenus() {
        cmbMenu.removeAllItems();
        cmbMenu.addItem(null);
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        if (r != null) {
            GetMenuDto dto = new GetMenuDto();
            dto.setRestaurantName(r.getName());
            menuController.findMenus(dto).forEach(cmbMenu::addItem);
        }
    }

    private void applyFilters() {
        String name = txtName.getText().trim();
        currentDto.setName(name.isEmpty() ? null : name);

        try {
            currentDto.setMoreThanPrice(
                    txtMinPrice.getText().trim().isEmpty()
                            ? 0
                            : Double.parseDouble(txtMinPrice.getText().trim())
            );
        } catch (NumberFormatException ignored) {
            currentDto.setMoreThanPrice(0);
        }

        try {
            currentDto.setLessThanPrice(
                    txtMaxPrice.getText().trim().isEmpty()
                            ? Double.MAX_VALUE
                            : Double.parseDouble(txtMaxPrice.getText().trim())
            );
        } catch (NumberFormatException ignored) {
            currentDto.setLessThanPrice(Double.MAX_VALUE);
        }

        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        currentDto.setRestaurantId(r != null ? r.getId() : 0);

        Menu m = (Menu) cmbMenu.getSelectedItem();
        currentDto.setMenuId(m != null ? m.getId() : 0);

        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        cmbRestaurant.setSelectedIndex(0);
        cmbMenu.removeAllItems();
        cmbMenu.addItem(null);
        txtName.setText("");
        txtMinPrice.setText("");
        txtMaxPrice.setText("");
        currentDto.setName(null);
        currentDto.setMoreThanPrice(0);
        currentDto.setLessThanPrice(Double.MAX_VALUE);
        currentDto.setRestaurantId(0);
        currentDto.setMenuId(0);
        currentDto.setPage(0);
        sortBy = "updatedAt";
        sortDir = "desc";
    }

    private void loadData() {
        model.setRowCount(0);
        List<MenuItem> list = menuItemController.findMenuItems(currentDto);
        if ("price".equals(sortBy)) {
            list.sort(Comparator.comparing(MenuItem::getPrice));
            if ("desc".equals(sortDir)) Collections.reverse(list);
        } else if ("ordered".equals(sortBy)) {
            list.sort(Comparator.comparing(MenuItem::getTotalOrderedCount));
            if ("desc".equals(sortDir)) Collections.reverse(list);
        }
        for (MenuItem item : list) {
            model.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getMenu().getName(),
                    item.getMenu().getRestaurant().getName(),
                    String.format("%.2f", item.getPrice()),
                    item.getTotalOrderedCount()
            });
        }
        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(list.size() == currentDto.getSize());
    }

    private void openForm(MenuItem item) {
        MenuItemFormDialog dlg = new MenuItemFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                item,
                () -> {
                    currentDto.setPage(0);
                    if (item != null) {
                        currentDto.setRestaurantId(item.getMenu().getRestaurant().getId());
                        currentDto.setMenuId(item.getMenu().getId());
                        cmbRestaurant.setSelectedItem(item.getMenu().getRestaurant());
                        cmbMenu.setSelectedItem(item.getMenu());
                    }
                    loadData();
                }
        );
        dlg.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
        if (JOptionPane.showConfirmDialog(this, "Delete item #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            menuItemController.deleteMenuItem(id);
            currentDto.setPage(0);
            loadData();
        }
    }
}

package com.restaurant.views.Menu;

import com.restaurant.controllers.MenuController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;
import com.restaurant.models.Restaurant;
import com.restaurant.di.Injector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MenuListView extends JPanel {
    private static final String[] COLUMNS = {"ID", "Name", "Restaurant", "Description"};
    private final MenuController menuController;
    private final RestaurantController restaurantController;
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtName = new JTextField(10);
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JButton btnFilter = new JButton("Filter");
    private final JButton btnReset  = new JButton("Reset");
    private final JButton btnAdd    = new JButton("Add");
    private final JButton btnDel    = new JButton("Delete");
    private final JButton btnView   = new JButton("View Menu");
    private final JButton btnPrev   = new JButton("Previous");
    private final JButton btnNext   = new JButton("Next");
    private final GetMenuDto currentDto = new GetMenuDto();

    public MenuListView() {
        menuController       = Injector.getInstance().getInstance(MenuController.class);
        restaurantController = Injector.getInstance().getInstance(RestaurantController.class);

        setLayout(new BorderLayout(10, 10));

        JPanel filters = new JPanel();
        filters.add(new JLabel("Name:"));
        filters.add(txtName);
        filters.add(new JLabel("Restaurant:"));
        cmbRestaurant.addItem(null);
        restaurantController.findAllRestaurants().forEach(cmbRestaurant::addItem);
        cmbRestaurant.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean foc
            ) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                setText(value instanceof Restaurant ? ((Restaurant) value).getName() : "");
                return this;
            }
        });
        filters.add(cmbRestaurant);
        filters.add(btnFilter);
        filters.add(btnReset);
        filters.add(btnAdd);
        filters.add(btnDel);
        filters.add(btnView);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c
            ) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel
                        ? tbl.getSelectionBackground()
                        : (r % 2 == 0 ? Color.WHITE : new Color(245,245,245))
                );
                setForeground(Color.DARK_GRAY);
                return this;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        btnFilter.addActionListener(e -> applyFilters());
        btnReset.addActionListener(e -> { resetFilters(); loadData(); });
        btnAdd.addActionListener(e -> openForm(null));
        btnDel.addActionListener(e -> deleteSelected());
        btnView.addActionListener(e -> viewDetail());
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

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
                        openForm(menuController.getMenu(id));
                    }
                }
            }
        });

        resetFilters();
        loadData();
    }

    private void applyFilters() {
        String name = txtName.getText().trim();
        currentDto.setName(name.isEmpty() ? null : name);
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        currentDto.setRestaurantName(r != null ? r.getName() : null);
        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        txtName.setText("");
        cmbRestaurant.setSelectedIndex(0);
        currentDto.setName(null);
        currentDto.setRestaurantName(null);
        currentDto.setSortBy("updatedAt");
        currentDto.setSortDir("desc");
        currentDto.setPage(0);
    }

    private void loadData() {
        model.setRowCount(0);
        List<Menu> list = menuController.findMenus(currentDto);
        for (Menu m : list) {
            model.addRow(new Object[]{ m.getId(), m.getName(), m.getRestaurant().getName(), m.getDescription() });
        }
        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(list.size() == currentDto.getSize());
    }

    private void openForm(Menu m) {
        MenuFormDialog dlg = new MenuFormDialog((Frame) SwingUtilities.getWindowAncestor(this), m, this::loadData);
        dlg.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a menu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
        if (JOptionPane.showConfirmDialog(this, "Delete menu #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            menuController.deleteMenu(id);
            loadData();
        }
    }

    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu to view details.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
        MenuDetailDialog dlg = new MenuDetailDialog((Frame) SwingUtilities.getWindowAncestor(this), id);
        dlg.setVisible(true);
    }
}

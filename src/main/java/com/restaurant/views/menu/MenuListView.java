package com.restaurant.views.menu;

import com.restaurant.controllers.MenuController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;
import com.restaurant.models.Restaurant;
import com.restaurant.views.LoadableView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MenuListView extends JPanel implements LoadableView {
    private static final String[] COLUMNS = {"ID", "Name", "Restaurant", "Description"};
    private final MenuController menuController;
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtName = new JTextField(10);
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final GetMenuDto currentDto = new GetMenuDto();

    public MenuListView() {
        menuController = Injector.getInstance().getInstance(MenuController.class);
        RestaurantController restaurantController = Injector.getInstance().getInstance(RestaurantController.class);

        setLayout(new BorderLayout(10, 10));

        JPanel filters = new JPanel();
        filters.add(new JLabel("Name:"));
        filters.add(txtName);
        filters.add(new JLabel("Restaurant:"));
        cmbRestaurant.addItem(null);
        restaurantController.findAllRestaurants().forEach(cmbRestaurant::addItem);
        cmbRestaurant.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean foc
            ) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                setText(value instanceof Restaurant ? ((Restaurant) value).getName() : "");
                return this;
            }
        });
        filters.add(cmbRestaurant);
        JButton btnReset = new JButton("Reset");
        filters.add(btnReset);
        JButton btnAdd = new JButton("Add");
        filters.add(btnAdd);
        JButton btnDel = new JButton("Delete");
        filters.add(btnDel);
        JButton btnView = new JButton("View Menu");
        filters.add(btnView);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c
            ) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel
                        ? tbl.getSelectionBackground()
                        : (r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245))
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

        txtName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });
        cmbRestaurant.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) applyFilters();
        });
        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });
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
            @Override
            public void mouseClicked(MouseEvent e) {
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
        currentDto.setSortBy("id");
        currentDto.setSortDir("desc");
        currentDto.setPage(0);
    }

    @Override
    public void loadData() {
        model.setRowCount(0);
        List<Menu> list = menuController.findMenus(currentDto);
        for (Menu m : list) {
            model.addRow(new Object[]{m.getId(), m.getName(), m.getRestaurant().getName(), m.getDescription()});
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

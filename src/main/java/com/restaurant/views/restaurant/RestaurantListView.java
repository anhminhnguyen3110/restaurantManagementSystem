package com.restaurant.views.restaurant;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.models.Restaurant;
import com.restaurant.views.LoadableView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RestaurantListView extends JPanel implements LoadableView {
    private static final String[] COLUMNS = {
            "ID", "Name", "Address", "Status", "Width", "Height"
    };

    private final RestaurantController controller;
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtName = new JTextField(8);
    private final JTextField txtAddress = new JTextField(8);
    private final JComboBox<RestaurantStatus> cmbStatus;
    private final GetRestaurantDto currentDto = new GetRestaurantDto();
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");

    public RestaurantListView() {
        controller = Injector.getInstance()
                .getInstance(RestaurantController.class);

        DefaultComboBoxModel<RestaurantStatus> statusModel = new DefaultComboBoxModel<>();
        statusModel.addElement(null);
        for (RestaurantStatus s : RestaurantStatus.values()) {
            statusModel.addElement(s);
        }
        cmbStatus = new JComboBox<>(statusModel);
        cmbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });

        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel();
        top.add(new JLabel("Name:"));
        top.add(txtName);
        top.add(new JLabel("Address:"));
        top.add(txtAddress);
        top.add(new JLabel("Status:"));
        top.add(cmbStatus);
        JButton btnFilter = new JButton("Filter");
        top.add(btnFilter);
        JButton btnReset = new JButton("Reset");
        top.add(btnReset);
        JButton btnAdd = new JButton("Add");
        top.add(btnAdd);
        add(top, BorderLayout.NORTH);

        btnFilter.addActionListener(e -> applyFilters());
        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });
        btnAdd.addActionListener(e -> openForm(null));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
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
                setBackground(sel ? tbl.getSelectionBackground()
                        : (r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)));
                setForeground(Color.DARK_GRAY);
                return this;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Restaurant r = getSelected();
                    if (r != null) openForm(r);
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
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
        add(paging, BorderLayout.SOUTH);

        resetFilters();
    }

    private void applyFilters() {
        currentDto.setName(txtName.getText().trim().isEmpty()
                ? null : txtName.getText().trim());
        currentDto.setAddress(txtAddress.getText().trim().isEmpty()
                ? null : txtAddress.getText().trim());
        currentDto.setStatus((RestaurantStatus) cmbStatus.getSelectedItem());
        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        txtName.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
        currentDto.setName(null);
        currentDto.setAddress(null);
        currentDto.setStatus(null);
        currentDto.setSortBy("id");
        currentDto.setSortDir("desc");
        currentDto.setPage(0);
    }

    @Override
    public void loadData() {
        model.setRowCount(0);
        List<Restaurant> list = controller.findRestaurants(currentDto);
        for (Restaurant r : list) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    r.getAddress(),
                    r.getStatus(),
                    r.getMaxX(),
                    r.getMaxY()
            });
        }
        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(list.size() == currentDto.getSize());
    }

    private Restaurant getSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a restaurant.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int id = (int) model.getValueAt(table.convertRowIndexToModel(r), 0);
        return controller.getRestaurantById(id);
    }

    private void openForm(Restaurant existing) {
        RestaurantFormDialog dlg = new RestaurantFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                existing,
                this::loadData
        );
        dlg.setVisible(true);
    }
}
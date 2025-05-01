package com.restaurant.views.orderItem;

import com.restaurant.controllers.OrderItemController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import com.restaurant.models.Shipment;
import com.restaurant.constants.OrderItemStatus;
import com.restaurant.views.order.AddOrderItemDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class OrderItemListView extends JPanel {
    private final Order order;
    private final Runnable onUpdated;
    private final OrderItemController orderItemController;
    private final RestaurantController restaurantController;
    private final GetOrderItemDto dto = new GetOrderItemDto();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtIdFilter = new JTextField(5);
    private final JTextField txtMenuFilter = new JTextField(10);
    private final JComboBox<OrderItemStatus> cbStatusFilter = new JComboBox<>();
    private final JComboBox<Restaurant> cbRestaurantFilter = new JComboBox<>();
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final JButton btnAdd = new JButton("Add");

    public OrderItemListView(Order order, Runnable onUpdated) {
        super(new BorderLayout(5,5));
        this.order = order;
        this.onUpdated = onUpdated;
        this.orderItemController = Injector.getInstance().getInstance(OrderItemController.class);
        this.restaurantController = Injector.getInstance().getInstance(RestaurantController.class);

        List<Restaurant> restaurants = restaurantController.findAllRestaurants();
        for (Restaurant r : restaurants) cbRestaurantFilter.addItem(r);
        if (!restaurants.isEmpty()) {
            cbRestaurantFilter.setSelectedIndex(0);
            dto.setRestaurantId(restaurants.get(0).getId());
        }

        JPanel filters = new JPanel();
        filters.add(new JLabel("ID:"));
        filters.add(txtIdFilter);
        filters.add(new JLabel("Item Name:"));
        filters.add(txtMenuFilter);
        filters.add(new JLabel("Status:"));
        cbStatusFilter.addItem(null);
        for (OrderItemStatus s : OrderItemStatus.values()) cbStatusFilter.addItem(s);
        cbStatusFilter.setSelectedIndex(0);
        filters.add(cbStatusFilter);
        filters.add(new JLabel("Restaurant:"));
        filters.add(cbRestaurantFilter);
        JButton btnReset = new JButton("Reset");
        filters.add(btnReset);
        filters.add(btnAdd);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID","Menu Item","Qty","Notes","Status","Type","Location"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setRowHeight(60);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
        }
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        dto.setPage(0);
        dto.setSize(20);
        dto.setSortBy("id");
        dto.setSortDir("asc");

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        txtIdFilter.getDocument().addDocumentListener(dl);
        txtMenuFilter.getDocument().addDocumentListener(dl);
        cbStatusFilter.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });
        cbRestaurantFilter.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });

        btnReset.addActionListener(e -> {
            txtIdFilter.setText("");
            txtMenuFilter.setText("");
            cbStatusFilter.setSelectedIndex(0);
            if (!restaurants.isEmpty()) cbRestaurantFilter.setSelectedIndex(0);
            dto.setSortBy("id");
            dto.setSortDir("asc");
            applyFilters();
        });

        btnAdd.addActionListener(e -> {
            Dialog owner = (Dialog) SwingUtilities.getWindowAncestor(this);
            new AddOrderItemDialog(owner, order, v -> {
                loadData();
                onUpdated.run();
            }).setVisible(true);
        });

        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String key = switch (col) {
                    case 1 -> "menuItemName";
                    case 2 -> "quantity";
                    case 3 -> "customization";
                    case 4 -> "status";
                    default -> null;
                };
                if (key != null) {
                    if (key.equals(dto.getSortBy())) dto.setSortDir(dto.getSortDir().equals("asc") ? "desc" : "asc");
                    else { dto.setSortBy(key); dto.setSortDir("asc"); }
                    dto.setPage(0);
                    loadData();
                }
            }
        });

        btnPrev.addActionListener(e -> {
            if (dto.getPage() > 0) {
                dto.setPage(dto.getPage() - 1);
                loadData();
            }
        });
        btnNext.addActionListener(e -> {
            dto.setPage(dto.getPage() + 1);
            loadData();
        });

        loadData();
    }

    private void applyFilters() {
        try { dto.setId(Integer.parseInt(txtIdFilter.getText().trim())); }
        catch (Exception ignored) { dto.setId(0); }
        String menuTxt = txtMenuFilter.getText().trim();
        dto.setMenuItemName(menuTxt.isEmpty() ? null : menuTxt);
        OrderItemStatus st = (OrderItemStatus) cbStatusFilter.getSelectedItem();
        dto.setStatus(st == null ? null : st.name());
        Restaurant selRest = (Restaurant) cbRestaurantFilter.getSelectedItem();
        dto.setRestaurantId(selRest == null ? 0 : selRest.getId());
        dto.setPage(0);
        loadData();
    }

    public void loadData() {
        dto.setOrderId(order != null ? order.getId() : 0);
        List<OrderItem> items = orderItemController.findOrderItems(dto);
        model.setRowCount(0);
        for (OrderItem i : items) {
            Order o = i.getOrder();
            RestaurantTable rt = o.getRestaurantTable();
            Shipment sh = o.getShipment();
            String type = rt != null ? "Dine In" : "Delivery";
            String location = rt != null
                    ? "Table #" + rt.getNumber()
                    : (sh != null ? "Tracking #" + sh.getTrackingNumber() : "");
            model.addRow(new Object[]{
                    i.getId(),
                    i.getMenuItem().getName(),
                    i.getQuantity(),
                    i.getCustomization(),
                    i.getStatus(),
                    type,
                    location
            });
        }
        btnPrev.setEnabled(dto.getPage() > 0);
        btnNext.setEnabled(items.size() == dto.getSize());
        onUpdated.run();
    }

    private static class TextAreaRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
            setMargin(new Insets(0,0,0,0));
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setFont(table.getFont());
            return this;
        }
    }
}

package com.restaurant.views.order;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.controllers.OrderController;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.models.Order;
import com.restaurant.models.Restaurant;
import com.restaurant.views.LoadableView;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class OrderListView extends JPanel implements LoadableView {
    private static final String[] COLUMNS = {
            "ID", "Restaurant", "Table", "Type", "Status", "Total Price", "Created"
    };

    private final OrderController orderController;
    private final DefaultTableModel model;
    private final JTable table;
    private final GetOrderDto currentDto = new GetOrderDto();
    private final JComboBox<OrderType> cbType = new JComboBox<>(OrderType.values());
    private final JComboBox<OrderStatus> cbStatus = new JComboBox<>(OrderStatus.values());
    private final JComboBox<Restaurant> cbRestaurant = new JComboBox<>();
    private final JXDatePicker dpDate = new JXDatePicker();
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");

    public OrderListView() {
        this.orderController = Injector.getInstance().getInstance(OrderController.class);
        RestaurantController restaurantController = Injector.getInstance().getInstance(RestaurantController.class);

        setLayout(new BorderLayout(10, 10));

        List<Restaurant> restaurants = restaurantController.findAllRestaurants();
        for (Restaurant r : restaurants) {
            cbRestaurant.addItem(r);
        }
        if (!restaurants.isEmpty()) {
            cbRestaurant.setSelectedIndex(0);
            currentDto.setRestaurantId(restaurants.get(0).getId());
        }

        JPanel top = new JPanel();
        top.add(new JLabel("Date:"));
        top.add(dpDate);
        top.add(new JLabel("Type:"));
        top.add(cbType);
        top.add(new JLabel("Status:"));
        top.add(cbStatus);
        top.add(new JLabel("Restaurant:"));
        top.add(cbRestaurant);
        JButton btnReset = new JButton("Reset");
        top.add(btnReset);
        JButton btnAdd = new JButton("Add");
        top.add(btnAdd);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        dpDate.addActionListener(e -> applyFilters());
        cbType.addActionListener(e -> applyFilters());
        cbStatus.addActionListener(e -> applyFilters());
        cbRestaurant.addActionListener(e -> applyFilters());

        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });
        btnAdd.addActionListener(e -> openForm());

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
                    Order o = getSelectedOrder();
                    if (o != null) {
                        new OrderUpdateDialog(
                                (Frame) SwingUtilities.getWindowAncestor(OrderListView.this),
                                o,
                                OrderListView.this::loadData
                        ).setVisible(true);
                    }
                }
            }
        });

        resetFilters();
    }

    private void applyFilters() {
        currentDto.setDate(
                dpDate.getDate() == null ? null
                        : dpDate.getDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
        );
        currentDto.setOrderType((OrderType) cbType.getSelectedItem());
        currentDto.setStatus(cbStatus.getSelectedItem() == null
                ? null
                : (OrderStatus) cbStatus.getSelectedItem()
        );
        Restaurant sel = (Restaurant) cbRestaurant.getSelectedItem();
        currentDto.setRestaurantId(sel == null ? 0 : sel.getId());
        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        dpDate.setDate(null);
        cbType.setSelectedIndex(-1);
        cbStatus.setSelectedIndex(-1);
        if (cbRestaurant.getItemCount() > 0) cbRestaurant.setSelectedIndex(0);

        currentDto.setDate(null);
        currentDto.setOrderType(null);
        currentDto.setStatus(null);
        currentDto.setRestaurantId(
                cbRestaurant.getItemCount() > 0
                        ? ((Restaurant) Objects.requireNonNull(cbRestaurant.getSelectedItem())).getId()
                        : 0
        );
        currentDto.setPage(0);
        currentDto.setSize(20);
        currentDto.setSortBy("id");
        currentDto.setSortDir("desc");
    }

    @Override
    public void loadData() {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        int pageSize = currentDto.getSize();
        currentDto.setSize(pageSize + 1);
        List<Order> fetched = orderController.findOrders(currentDto);

        boolean hasNext = fetched.size() > pageSize;
        List<Order> toShow = hasNext
                ? fetched.subList(0, pageSize)
                : fetched;

        for (Order o : toShow) {
            model.addRow(new Object[]{
                    o.getId(),
                    o.getRestaurant().getName(),
                    o.getRestaurantTable() != null
                            ? o.getRestaurantTable().getNumber()
                            : "",
                    o.getOrderType(),
                    o.getStatus(),
                    String.format("%.2f", o.getTotalPrice()),
                    fmt.format(o.getCreatedAt())
            });
        }

        currentDto.setSize(pageSize);

        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(hasNext);
    }

    private void openForm() {
        new OrderFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                this::loadData
        ).setVisible(true);
    }

    private Order getSelectedOrder() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(
                    this, "Please select an order.", "Info", JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        }
        int id = (int) model.getValueAt(
                table.convertRowIndexToModel(r), 0
        );
        return orderController.getOrder(id);
    }
}

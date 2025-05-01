package com.restaurant.views.order;

import com.restaurant.constants.OrderType;
import com.restaurant.controllers.*;
import com.restaurant.di.Injector;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import com.restaurant.models.RestaurantTable;
import com.restaurant.models.Shipment;
import com.restaurant.utils.validators.OrderInputValidator;
import com.restaurant.views.orderItem.OrderItemFormDialog;
import com.restaurant.views.payment.PaymentFormDialog;
import com.restaurant.views.shipment.ShipmentFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class OrderUpdateDialog extends JDialog {
    private final OrderController orderController = Injector.getInstance().getInstance(OrderController.class);
    private final OrderItemController itemController = Injector.getInstance().getInstance(OrderItemController.class);
    private final PaymentController paymentController = Injector.getInstance().getInstance(PaymentController.class);
    private final ShipmentController shipmentController = Injector.getInstance().getInstance(ShipmentController.class);
    private final Order order;
    private final Runnable onSaved;
    private final JComboBox<OrderType> cbType = new JComboBox<>(OrderType.values());
    private final JLabel lblTable = new JLabel("Table:");
    private final JComboBox<RestaurantTable> cbTable = new JComboBox<>();
    private final JButton btnSaveOrder = new JButton("Save Order");
    private final DefaultTableModel itemModel = new DefaultTableModel(
            new String[]{"ID", "Menu", "Qty", "Total Price", "Notes", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable itemTable = new JTable(itemModel);
    private final JButton btnAddItem = new JButton("Add");
    private final JButton btnDelItem = new JButton("Delete");
    private final GetPaymentDto payDto = new GetPaymentDto();
    private final JLabel lblPaymentMethod = new JLabel("Method: ");
    private final JLabel lblPaymentAmount = new JLabel("Amount: ");
    private final JLabel lblPaymentChange = new JLabel("Change: ");
    private final JButton btnEditPayment = new JButton("Add Payment");
    private final GetShipmentDto shipDto = new GetShipmentDto();
    private final JLabel lblShipmentService = new JLabel("Service: ");
    private final JLabel lblShipmentShipper = new JLabel("Shipper: ");
    private final JLabel lblShipmentStatus = new JLabel("Status: ");
    private final JLabel lblShipmentTracking = new JLabel("Tracking #: ");
    private final JButton btnEditShipment = new JButton("Add Shipment");
    private final JPanel shipmentPanel;
    private final JSplitPane rightSplit;
    private Payment currentPayment;
    private Shipment currentShipment;
    private OrderType originalType;
    private int originalTableId;

    public OrderUpdateDialog(Frame owner, Order existing, Runnable onSaved) {
        super(owner, "Update Order #" + existing.getId(), true);
        this.order = existing;
        this.onSaved = onSaved;
        this.originalType = existing.getOrderType();
        this.originalTableId = existing.getRestaurantTable() != null
                ? existing.getRestaurantTable().getId() : 0;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                onSaved.run();
            }
        });
        RestaurantTableController tableController = Injector.getInstance().getInstance(RestaurantTableController.class);
        for (RestaurantTable t : tableController.findAllTablesForOrder(existing.getRestaurant().getId())) {
            cbTable.addItem(t);
        }
        cbType.setSelectedItem(existing.getOrderType());
        cbTable.setSelectedItem(existing.getRestaurantTable());
        cbType.addActionListener(e -> {
            updateTypePanels();
            checkSaveEnabled();
        });
        cbTable.addActionListener(e -> checkSaveEnabled());
        btnSaveOrder.setEnabled(false);
        btnSaveOrder.addActionListener(e -> onSaveUpdate());
        JPanel orderPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));
        orderPanel.add(new JLabel("Type:"));
        orderPanel.add(cbType);
        orderPanel.add(lblTable);
        orderPanel.add(cbTable);
        orderPanel.add(btnSaveOrder);
        itemTable.setDefaultEditor(Object.class, null);
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createTitledBorder("Items"));
        itemPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        JPanel ip = new JPanel();
        ip.add(btnAddItem);
        ip.add(btnDelItem);
        itemPanel.add(ip, BorderLayout.SOUTH);
        btnAddItem.addActionListener(e -> {
            if (currentPayment != null) {
                JOptionPane.showMessageDialog(this, "Cannot modify items after payment is recorded.", "Action not allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new OrderItemFormDialog(owner, order, null, this::loadItems).setVisible(true);
        });
        btnDelItem.addActionListener(e -> {
            if (currentPayment != null) {
                JOptionPane.showMessageDialog(this, "Cannot modify items after payment is recorded.", "Action not allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int r = itemTable.getSelectedRow();
            if (r < 0) return;
            int id = (int) itemModel.getValueAt(itemTable.convertRowIndexToModel(r), 0);
            itemController.deleteOrderItem(id);
            loadItems();
        });
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (currentPayment != null) {
                        JOptionPane.showMessageDialog(OrderUpdateDialog.this, "Cannot modify items after payment is recorded.", "Action not allowed", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int r = itemTable.getSelectedRow();
                    if (r < 0) return;
                    int id = (int) itemModel.getValueAt(itemTable.convertRowIndexToModel(r), 0);
                    new OrderItemFormDialog(owner, order, itemController.getOrderItem(id), OrderUpdateDialog.this::loadItems).setVisible(true);
                }
            }
        });
        btnEditPayment.addActionListener(e -> {
            if (currentPayment == null) {
                new PaymentFormDialog(owner, order.getId(), this::loadPayments).setVisible(true);
            }
        });
        JPanel paymentPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment"));
        paymentPanel.add(lblPaymentMethod);
        paymentPanel.add(lblPaymentAmount);
        paymentPanel.add(lblPaymentChange);
        paymentPanel.add(btnEditPayment);
        btnEditShipment.addActionListener(e -> {
            if (currentShipment == null)
                new ShipmentFormDialog(owner, order.getId(), this::loadShipments).setVisible(true);
            else
                new ShipmentFormDialog(owner, currentShipment, this::loadShipments).setVisible(true);
        });
        shipmentPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        shipmentPanel.setBorder(BorderFactory.createTitledBorder("Shipment"));
        shipmentPanel.add(lblShipmentService);
        shipmentPanel.add(lblShipmentShipper);
        shipmentPanel.add(lblShipmentStatus);
        shipmentPanel.add(lblShipmentTracking);
        shipmentPanel.add(btnEditShipment);
        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paymentPanel, shipmentPanel);
        rightSplit.setResizeWeight(0.5);
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, itemPanel, rightSplit);
        mainSplit.setResizeWeight(0.3);
        setLayout(new BorderLayout(5, 5));
        add(orderPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel south = new JPanel();
        south.add(btnClose);
        add(south, BorderLayout.SOUTH);
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocation(0, 0);
        updateTypePanels();
        loadAll();
    }

    private void updateTypePanels() {
        boolean selectedDelivery = cbType.getSelectedItem() == OrderType.DELIVERY;
        lblTable.setVisible(!selectedDelivery);
        cbTable.setVisible(!selectedDelivery);
        boolean canShowShipment = selectedDelivery && originalType == OrderType.DELIVERY;
        shipmentPanel.setVisible(canShowShipment);
        rightSplit.setBottomComponent(canShowShipment ? shipmentPanel : new JPanel());
        rightSplit.revalidate();
        rightSplit.repaint();
    }

    private void onSaveUpdate() {
        OrderType type = (OrderType) cbType.getSelectedItem();
        if (type == OrderType.DINE_IN && currentShipment != null) {
            currentShipment = null;
        }
        int tableId = (cbTable.isVisible() && cbTable.getSelectedItem() != null)
                ? ((RestaurantTable) cbTable.getSelectedItem()).getId()
                : 0;
        List<String> errors = OrderInputValidator.validate(type, tableId);
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Errors", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UpdateOrderDto d = new UpdateOrderDto();
        d.setId(order.getId());
        d.setOrderType(type);
        d.setRestaurantTableId(tableId);
        orderController.updateOrder(d);
        originalType = type;
        originalTableId = tableId;
        btnSaveOrder.setText("Saved!");
        btnSaveOrder.setEnabled(false);
        updateTypePanels();
        loadAll();
    }

    private void checkSaveEnabled() {
        boolean changed = cbType.getSelectedItem() != originalType
                || ((RestaurantTable) Objects.requireNonNull(cbTable.getSelectedItem())).getId() != originalTableId;
        btnSaveOrder.setEnabled(changed);
    }

    private void loadAll() {
        loadItems();
        loadPayments();
        loadShipments();
    }

    private void loadItems() {
        itemModel.setRowCount(0);
        itemController.findOrderItems(new GetOrderItemDto() {{
            setOrderId(order.getId());
        }}).forEach(i -> {
            double totalPrice = i.getMenuItem().getPrice() * i.getQuantity();
            itemModel.addRow(new Object[]{
                    i.getId(),
                    i.getMenuItem().getName(),
                    i.getQuantity(),
                    totalPrice,
                    i.getCustomization(),
                    i.getStatus()
            });
        });
        onSaved.run();
    }

    private void loadPayments() {
        payDto.setOrderId(order.getId());
        payDto.setSize(1);
        List<Payment> payments = paymentController.findPayments(payDto);
        boolean paid = !payments.isEmpty();
        currentPayment = paid ? payments.get(0) : null;
        lblPaymentMethod.setText(paid ? "Method: " + currentPayment.getMethod() : "Method: ");
        lblPaymentAmount.setText(paid ? String.format("Amount: $%.2f", currentPayment.getUserPayAmount()) : "Amount: ");
        lblPaymentChange.setText(paid ? String.format("Change: $%.2f", currentPayment.getChangeAmount()) : "Change: ");
        btnEditPayment.setText(paid ? "Payment Recorded" : "Add Payment");
        btnEditPayment.setEnabled(!paid);
        btnAddItem.setEnabled(!paid);
        btnDelItem.setEnabled(!paid);
    }

    private void loadShipments() {
        if (cbType.getSelectedItem() != OrderType.DELIVERY) return;
        shipDto.setOrderId(order.getId());
        shipDto.setSize(1);
        List<Shipment> shipments = shipmentController.findShipments(shipDto);
        if (shipments.isEmpty()) {
            currentShipment = null;
            lblShipmentService.setText("Service: ");
            lblShipmentShipper.setText("Shipper: ");
            lblShipmentStatus.setText("Status: ");
            lblShipmentTracking.setText("Tracking #: ");
            btnEditShipment.setText("Add Shipment");
        } else {
            currentShipment = shipments.get(0);
            lblShipmentService.setText("Service: " + currentShipment.getServiceType());
            lblShipmentShipper.setText("Shipper: " + (currentShipment.getShipper() != null ? currentShipment.getShipper().getName() : ""));
            lblShipmentStatus.setText("Status: " + currentShipment.getStatus());
            lblShipmentTracking.setText("Tracking #: " + currentShipment.getTrackingNumber());
            btnEditShipment.setText("Edit Shipment");
        }
    }
}

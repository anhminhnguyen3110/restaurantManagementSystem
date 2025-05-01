package com.restaurant.views.shipment;

import com.restaurant.controllers.ShipmentController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.Shipment;
import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ShipmentListView extends JPanel {
    private final ShipmentController shipmentController;
    private final GetShipmentDto dto = new GetShipmentDto();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<ShipmentService> cbServiceFilter = new JComboBox<>();
    private final JComboBox<ShipmentStatus> cbStatusFilter = new JComboBox<>();
    private final JSpinner spinnerOrderId = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JTextField tfShipperName = new JTextField(10);
    private final JTextField tfCustomerName = new JTextField(10);
    private final JTextField tfTrackingNumber = new JTextField(10);
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final JButton btnReset = new JButton("Reset");
    private List<Shipment> currentShipments;

    public ShipmentListView() {
        super(new BorderLayout(5,5));
        shipmentController = Injector.getInstance().getInstance(ShipmentController.class);

        JPanel filters = new JPanel();
        filters.add(new JLabel("Service:"));
        cbServiceFilter.addItem(null);
        for (ShipmentService s : ShipmentService.values()) cbServiceFilter.addItem(s);
        filters.add(cbServiceFilter);
        filters.add(new JLabel("Status:"));
        cbStatusFilter.addItem(null);
        for (ShipmentStatus s : ShipmentStatus.values()) cbStatusFilter.addItem(s);
        filters.add(cbStatusFilter);
        filters.add(new JLabel("Order ID:"));
        filters.add(spinnerOrderId);
        filters.add(new JLabel("Shipper:"));
        filters.add(tfShipperName);
        filters.add(new JLabel("Customer:"));
        filters.add(tfCustomerName);
        filters.add(new JLabel("Tracking:"));
        filters.add(tfTrackingNumber);
        filters.add(btnReset);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID","Order ID","Restaurant","Service","Shipper","Customer","Status","Tracking"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        cbServiceFilter.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });
        cbStatusFilter.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });
        spinnerOrderId.addChangeListener(e -> applyFilters());
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        tfShipperName.getDocument().addDocumentListener(dl);
        tfCustomerName.getDocument().addDocumentListener(dl);
        tfTrackingNumber.getDocument().addDocumentListener(dl);

        btnReset.addActionListener(e -> {
            cbServiceFilter.setSelectedItem(null);
            cbStatusFilter.setSelectedItem(null);
            spinnerOrderId.setValue(0);
            tfShipperName.setText("");
            tfCustomerName.setText("");
            tfTrackingNumber.setText("");
            dto.setSortBy("id");
            dto.setSortDir("asc");
            dto.setPage(0);
            applyFilters();
        });

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String key = switch (col) {
                    case 1 -> "order.id";
                    case 2 -> "order.restaurant.name";
                    case 3 -> "serviceType";
                    case 4 -> "shipperName";
                    case 5 -> "customerName";
                    case 6 -> "status";
                    case 7 -> "trackingNumber";
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

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.convertRowIndexToModel(table.getSelectedRow());
                    Shipment s = currentShipments.get(row);
                    Frame owner = (Frame) SwingUtilities.getWindowAncestor(ShipmentListView.this);
                    new ShipmentFormDialog(owner, s, ShipmentListView.this::loadData).setVisible(true);
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

        dto.setPage(0);
        dto.setSize(20);
        dto.setSortBy("id");
        dto.setSortDir("asc");
        applyFilters();
    }

    private void applyFilters() {
        dto.setServiceType((ShipmentService) cbServiceFilter.getSelectedItem());
        dto.setStatus((ShipmentStatus) cbStatusFilter.getSelectedItem());
        dto.setOrderId((Integer) spinnerOrderId.getValue());
        String shipper = tfShipperName.getText().trim();
        dto.setShipperName(shipper.isEmpty() ? null : shipper);
        String customer = tfCustomerName.getText().trim();
        dto.setCustomerName(customer.isEmpty() ? null : customer);
        String tracking = tfTrackingNumber.getText().trim();
        dto.setTrackingNumber(tracking.isEmpty() ? null : tracking);
        dto.setPage(0);
        loadData();
    }

    public void loadData() {
        model.setRowCount(0);
        currentShipments = shipmentController.findShipments(dto);
        for (Shipment s : currentShipments) {
            model.addRow(new Object[]{
                    s.getId(),
                    s.getOrder().getId(),
                    s.getOrder().getRestaurant().getName(),
                    s.getServiceType(),
                    s.getShipper() != null ? s.getShipper().getName() : "",
                    s.getCustomer().getName(),
                    s.getStatus(),
                    s.getTrackingNumber()
            });
        }
        btnPrev.setEnabled(dto.getPage() > 0);
        btnNext.setEnabled(currentShipments.size() == dto.getSize());
    }
}

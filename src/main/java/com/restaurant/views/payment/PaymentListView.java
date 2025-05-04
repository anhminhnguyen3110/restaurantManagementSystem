package com.restaurant.views.payment;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import com.restaurant.controllers.PaymentController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Payment;
import com.restaurant.views.LoadableView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PaymentListView extends JPanel implements LoadableView {
    private final PaymentController paymentController;
    private final GetPaymentDto dto = new GetPaymentDto();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<PaymentMethod> cbMethodFilter = new JComboBox<>();
    private final JComboBox<PaymentStatus> cbStatusFilter = new JComboBox<>();
    private final JSpinner spinnerOrderId = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");

    public PaymentListView() {
        super(new BorderLayout(5, 5));
        paymentController = Injector.getInstance().getInstance(PaymentController.class);

        JPanel filters = new JPanel();
        filters.add(new JLabel("Method:"));
        cbMethodFilter.addItem(null);
        for (PaymentMethod m : PaymentMethod.values()) cbMethodFilter.addItem(m);
        filters.add(cbMethodFilter);

        filters.add(new JLabel("Status:"));
        cbStatusFilter.addItem(null);
        for (PaymentStatus s : PaymentStatus.values()) cbStatusFilter.addItem(s);
        filters.add(cbStatusFilter);

        filters.add(new JLabel("Order ID:"));
        filters.add(spinnerOrderId);

        JButton btnReset = new JButton("Reset");
        filters.add(btnReset);
        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Order ID", "Restaurant", "Method", "Paid", "Change", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        cbMethodFilter.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) applyFilters();
        });
        cbStatusFilter.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) applyFilters();
        });
        spinnerOrderId.addChangeListener(e -> applyFilters());

        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String key = switch (col) {
                    case 1 -> "order.id";
                    case 2 -> "order.restaurant.name";
                    case 3 -> "method";
                    case 4 -> "userPayAmount";
                    case 5 -> "changeAmount";
                    case 6 -> "status";
                    default -> null;
                };
                if (key != null) {
                    if (key.equals(dto.getSortBy())) dto.setSortDir(dto.getSortDir().equals("asc") ? "desc" : "asc");
                    else {
                        dto.setSortBy(key);
                        dto.setSortDir("asc");
                    }
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

        resetFilters();
    }

    private void resetFilters() {
        cbMethodFilter.setSelectedIndex(0);
        cbStatusFilter.setSelectedIndex(0);
        spinnerOrderId.setValue(0);
        dto.setMethod(null);
        dto.setStatus(null);
        dto.setOrderId(0);
        dto.setSortBy("id");
        dto.setSortDir("asc");
        dto.setPage(0);
    }

    private void applyFilters() {
        dto.setMethod((PaymentMethod) cbMethodFilter.getSelectedItem());
        dto.setStatus((PaymentStatus) cbStatusFilter.getSelectedItem());
        dto.setOrderId((Integer) spinnerOrderId.getValue());
        dto.setPage(0);
        loadData();
    }

    @Override
    public void loadData() {
        model.setRowCount(0);
        List<Payment> list = paymentController.findPayments(dto);
        for (Payment p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getOrder().getId(),
                    p.getOrder().getRestaurant().getName(),
                    p.getMethod(),
                    p.getUserPayAmount(),
                    p.getChangeAmount(),
                    p.getStatus()
            });
        }
        btnPrev.setEnabled(dto.getPage() > 0);
        btnNext.setEnabled(list.size() == dto.getSize());
    }
}

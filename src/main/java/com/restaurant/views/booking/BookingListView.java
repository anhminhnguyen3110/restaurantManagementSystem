package com.restaurant.views.booking;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.BookingController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.models.Booking;
import com.restaurant.views.LoadableView;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingListView extends JPanel implements LoadableView {
    private static final String[] COLUMNS = {
            "ID", "Customer", "Phone", "Restaurant", "Table", "#Seats", "Date", "Start", "End", "Status"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final BookingController bookingController;
    private final JTable table;
    private final DefaultTableModel model;
    private final JXDatePicker dpDate = new JXDatePicker();
    private final JComboBox<BookingTimeSlot> cbStartFilter;
    private final JComboBox<BookingTimeSlot> cbEndFilter;
    private final JTextField txtCust = new JTextField(8);
    private final JTextField txtPhone = new JTextField(8);
    private final JTextField txtTableNo = new JTextField(3);
    private final JComboBox<BookingStatus> cmbStatus;
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final GetBookingsDto currentDto = new GetBookingsDto();

    public BookingListView() {
        bookingController = Injector.getInstance().getInstance(BookingController.class);

        DefaultComboBoxModel<BookingTimeSlot> startModel = new DefaultComboBoxModel<>();
        startModel.addElement(null);
        for (BookingTimeSlot slot : BookingTimeSlot.values()) startModel.addElement(slot);
        cbStartFilter = new JComboBox<>(startModel);
        cbStartFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });

        DefaultComboBoxModel<BookingTimeSlot> endModel = new DefaultComboBoxModel<>();
        endModel.addElement(null);
        for (BookingTimeSlot slot : BookingTimeSlot.values()) endModel.addElement(slot);
        cbEndFilter = new JComboBox<>(endModel);
        cbEndFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });

        DefaultComboBoxModel<BookingStatus> statusModel = new DefaultComboBoxModel<>();
        statusModel.addElement(null);
        for (BookingStatus s : BookingStatus.values()) statusModel.addElement(s);
        cmbStatus = new JComboBox<>(statusModel);
        cmbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });

        setLayout(new BorderLayout(10, 10));

        JPanel filters = new JPanel();
        filters.add(new JLabel("Date:"));
        filters.add(dpDate);
        filters.add(new JLabel("Start:"));
        filters.add(cbStartFilter);
        filters.add(new JLabel("End:"));
        filters.add(cbEndFilter);
        filters.add(new JLabel("Cust Name:"));
        filters.add(txtCust);
        filters.add(new JLabel("Phone:"));
        filters.add(txtPhone);
        filters.add(new JLabel("Table #:"));
        filters.add(txtTableNo);
        filters.add(new JLabel("Status:"));
        filters.add(cmbStatus);
        JButton btnReset = new JButton("Reset");
        filters.add(btnReset);

        JPanel crud = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnDel = new JButton("Delete");
        crud.add(btnAdd);
        crud.add(btnDel);
        filters.add(crud);

        add(filters, BorderLayout.NORTH);

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
                setBackground(sel ? tbl.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)));
                setForeground(Color.DARK_GRAY);
                return this;
            }
        });
        table.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c
            ) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(sel ? tbl.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)));
                if (val instanceof BookingStatus st) {
                    switch (st) {
                        case COMPLETED -> setForeground(new Color(0, 128, 0));
                        case CANCELLED -> setForeground(new Color(192, 0, 0));
                        default -> setForeground(Color.BLUE);
                    }
                    setText(st.toString());
                } else {
                    setForeground(Color.DARK_GRAY);
                }
                return this;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev);
        paging.add(btnNext);
        add(paging, BorderLayout.SOUTH);

        DocumentListener dl = new DocumentListener() {
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
        };

        dpDate.addActionListener(e -> applyFilters());
        cbStartFilter.addActionListener(e -> applyFilters());
        cbEndFilter.addActionListener(e -> applyFilters());
        txtCust.getDocument().addDocumentListener(dl);
        txtPhone.getDocument().addDocumentListener(dl);
        txtTableNo.getDocument().addDocumentListener(dl);
        cmbStatus.addActionListener(e -> applyFilters());

        btnReset.addActionListener(e -> {
            resetFilters();
            loadData();
        });
        btnAdd.addActionListener(e -> openForm(null));
        btnDel.addActionListener(e -> deleteSelected());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int c = table.columnAtPoint(e.getPoint());
                String key = switch (c) {
                    case 6 -> "date";
                    case 7 -> "startTime";
                    case 8 -> "endTime";
                    case 9 -> "status";
                    default -> null;
                };
                if (key != null) {
                    if (key.equals(currentDto.getSortBy())) {
                        currentDto.setSortDir(currentDto.getSortDir().equals("asc") ? "desc" : "asc");
                    } else {
                        currentDto.setSortBy(key);
                        currentDto.setSortDir("asc");
                    }
                    currentDto.setPage(0);
                    loadData();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Booking b = getSelected();
                    if (b != null) openForm(b);
                }
            }
        });

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

        resetFilters();
    }

    private void applyFilters() {
        currentDto.setDate(
                dpDate.getDate() == null
                        ? null
                        : dpDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );
        currentDto.setStartTime((BookingTimeSlot) cbStartFilter.getSelectedItem());
        currentDto.setEndTime((BookingTimeSlot) cbEndFilter.getSelectedItem());
        currentDto.setCustomerName(txtCust.getText().trim());
        currentDto.setPhoneNumber(txtPhone.getText().trim());
        String t = txtTableNo.getText().trim();
        currentDto.setTableNumber(t.isEmpty() ? null : Integer.valueOf(t));
        currentDto.setStatus((BookingStatus) cmbStatus.getSelectedItem());
        currentDto.setPage(0);
        loadData();
    }

    private void resetFilters() {
        dpDate.setDate(null);
        cbStartFilter.setSelectedIndex(0);
        cbEndFilter.setSelectedIndex(0);
        txtCust.setText("");
        txtPhone.setText("");
        txtTableNo.setText("");
        cmbStatus.setSelectedIndex(0);
        currentDto.setDate(null);
        currentDto.setStartTime(null);
        currentDto.setEndTime(null);
        currentDto.setCustomerName("");
        currentDto.setPhoneNumber("");
        currentDto.setTableNumber(null);
        currentDto.setStatus(null);
        currentDto.setSortBy("id");
        currentDto.setSortDir("desc");
        currentDto.setPage(0);
    }

    private void deleteSelected() {
        Booking b = getSelected();
        if (b != null && JOptionPane.showConfirmDialog(
                this, "Delete booking #" + b.getId() + "?", "Confirm", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {
            bookingController.deleteBooking(b.getId());
            loadData();
        }
    }

    private Booking getSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int id = (int) model.getValueAt(table.convertRowIndexToModel(r), 0);
        return bookingController.getBooking(id);
    }

    private void openForm(Booking b) {
        BookingFormDialog dlg = new BookingFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                b,
                this::loadData
        );
        dlg.setVisible(true);
    }

    @Override
    public void loadData() {
        model.setRowCount(0);
        List<Booking> page = bookingController.findBookings(currentDto);
        for (Booking b : page) {
            model.addRow(new Object[]{
                    b.getId(),
                    b.getCustomer().getName(),
                    b.getCustomer().getPhoneNumber(),
                    b.getTable().getRestaurant().getName(),
                    b.getTable().getNumber(),
                    b.getTable().getCapacity(),
                    DATE_FMT.format(b.getDate()),
                    b.getStartTime().toString(),
                    b.getEndTime().toString(),
                    b.getStatus()
            });
        }
        btnPrev.setEnabled(currentDto.getPage() > 0);
        btnNext.setEnabled(page.size() == currentDto.getSize());
    }
}

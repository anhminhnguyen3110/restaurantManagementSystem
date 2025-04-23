package com.restaurant.views.Booking;

import com.restaurant.controllers.BookingController;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.models.Booking;
import com.restaurant.constants.BookingStatus;
import org.jdesktop.swingx.JXDatePicker;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class BookingListView extends JPanel {
    private final BookingController controller;
    private final JTable table;
    private final DefaultTableModel model;
    private final JXDatePicker dpFrom;
    private final JSpinner spFromTime;
    private final JXDatePicker dpTo;
    private final JSpinner spToTime;
    private final JTextField txtCustomer = new JTextField(10);
    private final JTextField txtPhone    = new JTextField(10);
    private final JTextField txtTableNum = new JTextField(3);
    private final JComboBox<BookingStatus> cmbStatus;
    private final JButton btnAdd     = new JButton("Add");
    private final JButton btnDelete  = new JButton("Delete");
    private final JButton btnFilter  = new JButton("Filter");
    private final JButton btnReset   = new JButton("Reset");
    private final JButton btnPrev    = new JButton("Previous");
    private final JButton btnNext    = new JButton("Next");
    private final GetBookingsDto currentDto = new GetBookingsDto();
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private static final String[] COLUMN_KEYS = {
            "id","customer","phone","restaurant","table","seats","duration","start","end","status"
    };

    public BookingListView(BookingController controller) {
        this.controller = controller;
        dpFrom = new JXDatePicker();
        spFromTime = new JSpinner(new SpinnerDateModel());
        spFromTime.setEditor(new JSpinner.DateEditor(spFromTime, "HH:mm"));
        dpTo = new JXDatePicker();
        spToTime = new JSpinner(new SpinnerDateModel());
        spToTime.setEditor(new JSpinner.DateEditor(spToTime, "HH:mm"));
        DefaultComboBoxModel<BookingStatus> statusModel = new DefaultComboBoxModel<>();
        statusModel.addElement(null);
        for (BookingStatus st : BookingStatus.values()) statusModel.addElement(st);
        cmbStatus = new JComboBox<>(statusModel);
        cmbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None" : ((BookingStatus) value).toString());
                return this;
            }
        });

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel filters = new JPanel();
        filters.add(new JLabel("From:"));      filters.add(dpFrom);      filters.add(spFromTime);
        filters.add(new JLabel("To:"));        filters.add(dpTo);        filters.add(spToTime);
        filters.add(new JLabel("Customer:"));  filters.add(txtCustomer);
        filters.add(new JLabel("Phone:"));     filters.add(txtPhone);
        filters.add(new JLabel("Table #:"));   filters.add(txtTableNum);
        filters.add(new JLabel("Status:"));    filters.add(cmbStatus);
        filters.add(btnAdd);                   filters.add(btnDelete);
        filters.add(btnFilter);                filters.add(btnReset);

        btnAdd.addActionListener(ev -> openForm(null));
        btnDelete.addActionListener(ev -> {
            Booking b = getSelected();
            if (b != null && JOptionPane.showConfirmDialog(this, "Delete booking #"+b.getId()+"?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                controller.deleteBooking(b.getId());
                loadData();
            }
        });
        btnFilter.addActionListener(e -> applyFilters());
        btnReset.addActionListener(e -> {
            dpFrom.setDate(null);     spFromTime.setValue(new Date());
            dpTo.setDate(null);       spToTime.setValue(new Date());
            txtCustomer.setText("");  txtPhone.setText("");  txtTableNum.setText("");
            cmbStatus.setSelectedIndex(0);
            currentDto.setFrom(null); currentDto.setTo(null);
            currentDto.setCustomerName("");
            currentDto.setPhoneNumber("");
            currentDto.setTableNumber(null);
            currentDto.setStatus(null);
            currentDto.setSortBy("start");
            currentDto.setSortDir("asc");
            currentDto.setPage(0);
            loadData();
        });

        add(filters, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID","Customer","Phone","Restaurant","Table","#Seats","Duration","Start","End","Status"}, 0
        ) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tbl, Object value, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl,value,sel,foc,row,col);
                setBackground(sel ? tbl.getSelectionBackground() : (row%2==0 ? Color.WHITE : new Color(245,245,245)));
                if(col==9 && value instanceof BookingStatus){
                    BookingStatus st=(BookingStatus)value;
                    setForeground(st==BookingStatus.COMPLETED ? new Color(0,128,0) : st==BookingStatus.CANCELLED ? new Color(192,0,0) : Color.BLUE);
                } else {
                    setForeground(Color.DARK_GRAY);
                }
                return this;
            }
        });

        table.getTableHeader().addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                int viewCol = table.columnAtPoint(e.getPoint());
                int modelCol = table.convertColumnIndexToModel(viewCol);
                String key = COLUMN_KEYS[modelCol];
                if (key.equals(currentDto.getSortBy())) {
                    currentDto.setSortDir(currentDto.getSortDir().equals("asc") ? "desc" : "asc");
                } else {
                    currentDto.setSortBy(key);
                    currentDto.setSortDir("asc");
                }
                currentDto.setPage(0);
                loadData();
            }
        });

        table.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    Booking b = getSelected();
                    if(b!=null) openForm(b);
                }
            }
        });

        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.add(makeToolButton("✏️ Edit","Edit",ev->{var b=getSelected(); if(b!=null)openForm(b);}));
        tools.add(makeToolButton("🔍 Details","Details",ev->{
            Booking b = getSelected();
            if(b!=null) new BookingDetailsDialog(SwingUtilities.getWindowAncestor(this), b).setVisible(true);
        }));
        add(tools, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paging = new JPanel();
        paging.add(btnPrev); paging.add(btnNext);
        btnPrev.addActionListener(e->{ if(currentDto.getPage()>0){ currentDto.setPage(currentDto.getPage()-1); loadData(); } });
        btnNext.addActionListener(e->{ currentDto.setPage(currentDto.getPage()+1); loadData(); });
        add(paging, BorderLayout.SOUTH);

        loadData();
    }

    private void applyFilters(){
        LocalDateTime from = null;
        if (dpFrom.getDate()!=null) {
            LocalDate d = dpFrom.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime t = ((Date)spFromTime.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            from = LocalDateTime.of(d,t);
        }
        LocalDateTime to = null;
        if (dpTo.getDate()!=null) {
            LocalDate d = dpTo.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime t = ((Date)spToTime.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            to = LocalDateTime.of(d,t);
        }
        currentDto.setFrom(from);
        currentDto.setTo(to);
        currentDto.setCustomerName(txtCustomer.getText().trim());
        currentDto.setPhoneNumber(txtPhone.getText().trim());
        String tn = txtTableNum.getText().trim();
        currentDto.setTableNumber(tn.isEmpty() ? null : Integer.valueOf(tn));
        currentDto.setStatus((BookingStatus)cmbStatus.getSelectedItem());
        currentDto.setPage(0);
        loadData();
    }

    private JButton makeToolButton(String label,String tip,java.awt.event.ActionListener l){
        JButton b=new JButton(label);
        b.setToolTipText(tip);
        b.addActionListener(l);
        b.setFocusable(false);
        return b;
    }

    private Booking getSelected(){
        int r = table.getSelectedRow();
        if(r<0){
            JOptionPane.showMessageDialog(this,"Please select a booking first.","Info",JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int id = (int)model.getValueAt(table.convertRowIndexToModel(r), 0);
        return controller.getBooking(id);
    }

    private void openForm(Booking booking){
        BookingFormDialog dlg = new BookingFormDialog((Frame)SwingUtilities.getWindowAncestor(this), controller, booking, this::loadData);
        dlg.setVisible(true);
    }

    private void loadData(){
        model.setRowCount(0);
        List<Booking> page = controller.findBookings(currentDto);
        for(Booking b: page){
            model.addRow(new Object[]{
                    b.getId(),
                    b.getCustomer().getName(),
                    b.getCustomer().getPhoneNumber(),
                    b.getTable().getRestaurant().getName(),
                    b.getTable().getNumber(),
                    b.getTable().getCapacity(),
                    b.getDuration(),
                    DISPLAY_FMT.format(b.getStart()),
                    DISPLAY_FMT.format(b.getEnd()),
                    b.getStatus()
            });
        }
        btnPrev.setEnabled(currentDto.getPage()>0);
        btnNext.setEnabled(page.size()==currentDto.getSize());
    }
}

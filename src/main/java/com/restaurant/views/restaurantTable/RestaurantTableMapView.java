package com.restaurant.views.restaurantTable;

import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import com.restaurant.views.LoadableView;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantTableMapView extends JPanel implements LoadableView {
    private final RestaurantTableController tableController;
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JXDatePicker datePicker = new JXDatePicker();
    private final JComboBox<BookingTimeSlot> cbTime;
    private final JPanel mapContainer = new JPanel(new GridBagLayout());
    private GetRestaurantTableDto searchDto;
    private GetRestaurantTableForBookingDto bookingDto;

    public RestaurantTableMapView() {
        RestaurantController restaurantController = Injector.getInstance().getInstance(RestaurantController.class);
        tableController = Injector.getInstance().getInstance(RestaurantTableController.class);
        searchDto = new GetRestaurantTableDto();
        bookingDto = new GetRestaurantTableForBookingDto();
        List<Restaurant> rests = restaurantController.findAllRestaurants();
        for (Restaurant r : rests) {
            cmbRestaurant.addItem(r);
        }
        if (!rests.isEmpty()) {
            cmbRestaurant.setSelectedIndex(0);
            searchDto.setRestaurantId(rests.get(0).getId());
        }
        DefaultComboBoxModel<BookingTimeSlot> timeModel = new DefaultComboBoxModel<>();
        timeModel.addElement(null);
        for (BookingTimeSlot slot : BookingTimeSlot.values()) {
            timeModel.addElement(slot);
        }
        cbTime = new JComboBox<>(timeModel);
        cbTime.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });
        cbTime.setSelectedIndex(0);
        JPanel top = new JPanel();
        top.add(new JLabel("Restaurant:"));
        top.add(cmbRestaurant);
        top.add(new JLabel("Date:"));
        top.add(datePicker);
        top.add(new JLabel("Time:"));
        top.add(cbTime);
        JButton btnReset = new JButton("Reset");
        top.add(btnReset);
        setLayout(new BorderLayout(5, 5));
        add(top, BorderLayout.NORTH);
        add(mapContainer, BorderLayout.CENTER);
        ActionListener refresher = e -> loadData();
        cmbRestaurant.addActionListener(refresher);
        datePicker.addActionListener(refresher);
        cbTime.addActionListener(refresher);
        btnReset.addActionListener(e -> {
            if (cmbRestaurant.getItemCount() > 0) {
                cmbRestaurant.setSelectedIndex(0);
                searchDto = new GetRestaurantTableDto();
                searchDto.setRestaurantId(cmbRestaurant.getItemAt(0).getId());
            }
            datePicker.setDate(null);
            cbTime.setSelectedIndex(0);
            bookingDto = new GetRestaurantTableForBookingDto();
            loadData();
        });
    }

    @Override
    public void loadData() {
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        if (r == null) {
            return;
        }
        searchDto.setRestaurantId(r.getId());
        List<RestaurantTable> all = tableController.findTables(searchDto);
        Date d = datePicker.getDate();
        BookingTimeSlot slot = (BookingTimeSlot) cbTime.getSelectedItem();
        Set<Integer> availIds = null;
        if (d != null && slot != null) {
            bookingDto.setRestaurantId(r.getId());
            bookingDto.setDate(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            bookingDto.setTime(slot);
            availIds = new HashSet<>();
            for (RestaurantTable t : tableController.findTablesForBooking(bookingDto)) {
                availIds.add(t.getId());
            }
        }
        TableMapPanel map = getTableMapPanel(r, all, availIds);
        mapContainer.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        mapContainer.add(map, gbc);
        mapContainer.revalidate();
        mapContainer.repaint();
    }

    private TableMapPanel getTableMapPanel(Restaurant r, List<RestaurantTable> all, Set<Integer> availIds) {
        TableMapPanel.Listener listener = new TableMapPanel.Listener() {
            @Override
            public void onExistingTable(RestaurantTable table, int cx, int cy) {
                openTableForm(table, r.getId(), cx, cy);
            }

            @Override
            public void onNewRegion(int sx, int sy, int ex, int ey) {
                openTableForm(null, r.getId(), sx, sy, ex, ey);
            }
        };
        return new TableMapPanel(r.getMaxX(), r.getMaxY(), all, availIds, listener);
    }

    private void openTableForm(RestaurantTable existing, int restaurantId, int... coords) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        RestaurantTableFormDialog dlg;
        if (existing != null) {
            dlg = new RestaurantTableFormDialog(owner, existing, restaurantId, this::loadData);
        } else {
            int sx = coords[0];
            int sy = coords[1];
            int ex = coords[2];
            int ey = coords[3];
            dlg = new RestaurantTableFormDialog(owner, null, restaurantId, sx, sy, ex, ey, this::loadData);
        }
        dlg.setVisible(true);
    }
}

package com.restaurant.views.RestaurantTable;

import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.RestaurantController;
import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantTableMapView extends JPanel {
    private final RestaurantController restaurantController;
    private final RestaurantTableController tableController;
    private final JComboBox<Restaurant> cmbRestaurant = new JComboBox<>();
    private final JXDatePicker datePicker = new JXDatePicker();
    private final JComboBox<BookingTimeSlot> cbTime = new JComboBox<>(BookingTimeSlot.values());
    private final JPanel mapContainer = new JPanel(new GridBagLayout());

    public RestaurantTableMapView() {
        restaurantController = Injector.getInstance().getInstance(RestaurantController.class);
        tableController = Injector.getInstance().getInstance(RestaurantTableController.class);
        List<Restaurant> rests = restaurantController.findAllRestaurants();
        for (Restaurant r : rests) cmbRestaurant.addItem(r);
        if (!rests.isEmpty()) cmbRestaurant.setSelectedIndex(0);
        JPanel top = new JPanel();
        top.add(new JLabel("Restaurant:")); top.add(cmbRestaurant);
        top.add(new JLabel("Date:"));       top.add(datePicker);
        top.add(new JLabel("Time:"));       top.add(cbTime);
        setLayout(new BorderLayout(5, 5));
        add(top, BorderLayout.NORTH);
        add(mapContainer, BorderLayout.CENTER);
        ActionListener refresher = e -> refreshMap();
        cmbRestaurant.addActionListener(refresher);
        datePicker.addActionListener(refresher);
        cbTime.addActionListener(refresher);
        refreshMap();
    }

    private void refreshMap() {
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        if (r == null) return;
        GetRestaurantTableDto gt = new GetRestaurantTableDto();
        gt.setRestaurantId(r.getId());
        List<RestaurantTable> all = tableController.findTables(gt);
        Date d = datePicker.getDate();
        BookingTimeSlot slot = (BookingTimeSlot) cbTime.getSelectedItem();
        Set<Integer> availIds = null;
        if (d != null && slot != null) {
            GetRestaurantTableForBookingDto bq = new GetRestaurantTableForBookingDto();
            bq.setRestaurantId(r.getId());
            bq.setDate(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            bq.setStartTime(slot);
            bq.setEndTime(slot);
            availIds = new HashSet<>();
            for (RestaurantTable t : tableController.findTablesForBooking(bq)) {
                availIds.add(t.getId());
            }
        }
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
        TableMapPanel map = new TableMapPanel(r.getMaxX(), r.getMaxY(), all, availIds, listener);
        mapContainer.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        mapContainer.add(map, gbc);
        mapContainer.revalidate();
        mapContainer.repaint();
    }

    private void openTableForm(RestaurantTable existing, int restaurantId, int... coords) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        RestaurantTableFormDialog dlg;
        if (existing != null) {
            dlg = new RestaurantTableFormDialog(owner, existing, restaurantId, this::refreshMap);
        } else {
            int sx = coords[0], sy = coords[1], ex = coords[2], ey = coords[3];
            dlg = new RestaurantTableFormDialog(owner, null, restaurantId, sx, sy, ex, ey, this::refreshMap);
        }
        dlg.setVisible(true);
    }
}

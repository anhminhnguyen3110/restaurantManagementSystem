package com.restaurant.views;

import com.restaurant.constants.UserRole;
import com.restaurant.views.booking.BookingListView;
import com.restaurant.views.menu.MenuListView;
import com.restaurant.views.menuItem.MenuItemListView;
import com.restaurant.views.order.OrderListView;
import com.restaurant.views.orderItem.OrderItemListView;
import com.restaurant.views.payment.PaymentListView;
import com.restaurant.views.restaurant.RestaurantListView;
import com.restaurant.views.restaurantTable.RestaurantTableMapView;
import com.restaurant.views.shipment.ShipmentListView;
import com.restaurant.views.user.UserListView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ListViewFactory {
    private static final Map<UserRole, List<ViewTab>> map = new EnumMap<>(UserRole.class);

    static {
        map.put(UserRole.OWNER, List.of(
                new ViewTab("Bookings", new BookingListView()),
                new ViewTab("Menus", new MenuListView()),
                new ViewTab("Menu Items", new MenuItemListView()),
                new ViewTab("Orders", new OrderListView()),
                new ViewTab("Order Items", new OrderItemListView(null, () -> {
                })),
                new ViewTab("Payments", new PaymentListView()),
                new ViewTab("Restaurants", new RestaurantListView()),
                new ViewTab("Tables", new RestaurantTableMapView()),
                new ViewTab("Shipments", new ShipmentListView()),
                new ViewTab("Users", new UserListView())
        ));
        map.put(UserRole.SHIPPER, List.of(
                new ViewTab("Shipments", new ShipmentListView())
        ));
        map.put(UserRole.MANAGER, List.of(
                new ViewTab("Bookings", new BookingListView()),
                new ViewTab("Menus", new MenuListView()),
                new ViewTab("Menu Items", new MenuItemListView()),
                new ViewTab("Orders", new OrderListView()),
                new ViewTab("Order Items", new OrderItemListView(null, () -> {
                })),
                new ViewTab("Payments", new PaymentListView()),
                new ViewTab("Restaurants", new RestaurantListView()),
                new ViewTab("Tables", new RestaurantTableMapView()),
                new ViewTab("Shipments", new ShipmentListView())
        ));
        map.put(UserRole.COOK, List.of(
                new ViewTab("Menu Items", new MenuItemListView()),
                new ViewTab("Order Items", new OrderItemListView(null, () -> {
                }))
        ));
        map.put(UserRole.WAIT_STAFF, List.of(
                new ViewTab("Orders", new OrderListView()),
                new ViewTab("Order Items", new OrderItemListView(null, () -> {
                })),
                new ViewTab("Menus", new MenuListView()),
                new ViewTab("Payments", new PaymentListView()),
                new ViewTab("Shipments", new ShipmentListView()),
                new ViewTab("Tables", new RestaurantTableMapView()),
                new ViewTab("Bookings", new BookingListView())
        ));
    }

    public static List<ViewTab> getTabsForRole(UserRole role) {
        return new ArrayList<>(map.getOrDefault(role, List.of()));
    }
}

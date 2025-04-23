package com.restaurant.views.Booking;

import com.restaurant.controllers.BookingController;
import com.restaurant.di.Injector;
import com.restaurant.views.BaseApp;

import javax.swing.*;

public class BookingApp extends BaseApp {
    public BookingApp(BookingController ctrl, BaseApp prevApp) {
        super("Booking Management", prevApp);
        getContentPane().add(new BookingListView(ctrl));
        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            BookingApp app = new BookingApp(Injector.getInstance().getInstance(BookingController.class), null);
            app.setVisible(true);
        });
    }
}
package com.restaurant.views;

import com.restaurant.constants.UserRole;
import com.restaurant.controllers.UserController;
import com.restaurant.di.Injector;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.models.User;
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

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JButton btnLogin = new JButton("Login");
    private final UserController userController;

    public MainView() {
        super("Login");
        initLookAndFeel();
        userController = Injector.getInstance().getInstance(UserController.class);
        buildLoginForm();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MainView app = new MainView();
            app.setVisible(true);
        });
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private void buildLoginForm() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(btnLogin, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(loginPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        LoginUserDto dto = new LoginUserDto();
        dto.setUsername(u);
        dto.setPassword(p);
        User user = userController.login(dto);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            return;
        }
        showDashboard(user);
    }

    private void showDashboard(User user) {
        getContentPane().removeAll();
        JTabbedPane tabs = new JTabbedPane();

        BookingListView bookingView = new BookingListView();
        MenuListView menuView = new MenuListView();
        MenuItemListView menuItemView = new MenuItemListView();
        OrderListView orderView = new OrderListView();
        OrderItemListView orderItemView = new OrderItemListView(null, () -> {
        });
        PaymentListView paymentView = new PaymentListView();
        RestaurantListView restaurantView = new RestaurantListView();
        RestaurantTableMapView tableView = new RestaurantTableMapView();
        ShipmentListView shipmentView = new ShipmentListView();
        UserListView userView = new UserListView();

        UserRole role = user.getRole();

        if (role == UserRole.OWNER) {
            tabs.addTab("Bookings", bookingView);
            tabs.addTab("Menus", menuView);
            tabs.addTab("Menu Items", menuItemView);
            tabs.addTab("Orders", orderView);
            tabs.addTab("Order Items", orderItemView);
            tabs.addTab("Payments", paymentView);
            tabs.addTab("Restaurants", restaurantView);
            tabs.addTab("Tables", tableView);
            tabs.addTab("Shipments", shipmentView);
            tabs.addTab("Users", userView);
            setTitle("Owner Dashboard");
        } else if (role == UserRole.SHIPPER) {
            tabs.addTab("Shipments", shipmentView);
            setTitle("Shipper Dashboard");
        } else if (role == UserRole.MANAGER) {
            tabs.addTab("Bookings", bookingView);
            tabs.addTab("Menus", menuView);
            tabs.addTab("Menu Items", menuItemView);
            tabs.addTab("Orders", orderView);
            tabs.addTab("Order Items", orderItemView);
            tabs.addTab("Payments", paymentView);
            tabs.addTab("Restaurants", restaurantView);
            tabs.addTab("Tables", tableView);
            tabs.addTab("Shipments", shipmentView);
            setTitle("Manager Dashboard");
        } else if (role == UserRole.COOK) {
            tabs.addTab("Menu Items", menuItemView);
            tabs.addTab("Order Items", orderItemView);
            setTitle("Cooker Dashboard");
        } else if (role == UserRole.WAIT_STAFF) {
            tabs.addTab("Orders", orderView);
            tabs.addTab("Order Items", orderItemView);
            tabs.addTab("Menus", menuView);
            tabs.addTab("Payments", paymentView);
            tabs.addTab("Shipments", shipmentView);
            tabs.addTab("Tables", tableView);
            tabs.addTab("Bookings", bookingView);
            setTitle("Wait Staff Dashboard");
        }

        JPanel detailTab = new JPanel(new GridBagLayout());
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(10, 10, 10, 10);
        dgbc.anchor = GridBagConstraints.CENTER;

        JPanel smallPanel = new JPanel();
        smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
        smallPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("My Profile"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        smallPanel.setMaximumSize(new Dimension(300, 180));

        JPanel info = new JPanel(new GridLayout(4, 2, 5, 5));
        info.add(new JLabel("Username:"));
        info.add(new JLabel(user.getUsername()));
        info.add(new JLabel("Name:"));
        info.add(new JLabel(user.getName()));
        info.add(new JLabel("Email:"));
        info.add(new JLabel(user.getEmail()));
        info.add(new JLabel("Role:"));
        info.add(new JLabel(role.toString()));
        smallPanel.add(info);
        smallPanel.add(Box.createVerticalStrut(10));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            dispose();
            launch();
        });
        smallPanel.add(btnLogout);

        detailTab.add(smallPanel, dgbc);
        tabs.addTab("My Profile", detailTab);

        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c == bookingView) bookingView.loadData();
            else if (c == menuView) menuView.loadData();
            else if (c == menuItemView) menuItemView.loadData();
            else if (c == orderView) orderView.loadData();
            else if (c == orderItemView) orderItemView.loadData();
            else if (c == paymentView) paymentView.loadData();
            else if (c == restaurantView) restaurantView.loadData();
            else if (c == tableView) tableView.loadData();
            else if (c == shipmentView) shipmentView.loadData();
            else if (c == userView) userView.loadData();
        });

        getContentPane().add(tabs, BorderLayout.CENTER);
        revalidate();
        repaint();
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}

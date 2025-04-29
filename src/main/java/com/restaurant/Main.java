package com.restaurant;

import com.restaurant.config.Env;
import com.restaurant.controllers.*;
import com.restaurant.controllers.impl.*;
import com.restaurant.daos.*;
import com.restaurant.daos.impl.*;
import com.restaurant.di.Injector;
import com.restaurant.models.*;
import com.restaurant.utils.DataSeeder;
import com.restaurant.views.order.OrderApp;
import com.restaurant.views.orderItem.OrderItemApp;
import com.restaurant.views.payment.PaymentApp;
import com.restaurant.views.restaurantTable.RestaurantTableApp;
import com.restaurant.views.shipment.ShipmentApp;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class Main {
    public static SessionFactory sessionFactory;

    public static void main(String[] args) {
        initSchema();
        injectDependencies();

        DataSeeder seeder = Injector.getInstance().getInstance(DataSeeder.class);
        seeder.seed();

        EntityManagerFactory emf =
                Injector.getInstance().getInstance(EntityManagerFactory.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emf.isOpen()) {
                emf.close();
                System.out.println("✔︎ SessionFactory closed via shutdown hook");
            }
        }));

//        BookingApp.launch();
//        MenuApp.launch();
//        RestaurantApp.launch();
        RestaurantTableApp.launch();
        OrderApp.launch();
//        OrderItemApp.launch();
//        PaymentApp.launch();
//        ShipmentApp.launch();
    }

    public static void initSchema() {
        Env env = Env.getInstance();

        Properties props = new Properties();
        props.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        props.put(Environment.URL, env.get("DB_URL"));
        props.put(Environment.USER, env.get("DB_USER"));
        props.put(Environment.PASS, env.get("DB_PASSWORD"));
        props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        props.put(Environment.HBM2DDL_AUTO, "create");
        props.put(Environment.SHOW_SQL, "true");

        var registry = new StandardServiceRegistryBuilder()
                .applySettings(props)
                .build();

        Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(Booking.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Menu.class)
                .addAnnotatedClass(MenuItem.class)
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(OrderItem.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Restaurant.class)
                .addAnnotatedClass(RestaurantTable.class)
                .addAnnotatedClass(Shipment.class)
                .addAnnotatedClass(User.class)
                .buildMetadata();

        sessionFactory = metadata.buildSessionFactory();
        System.out.println("✔︎ Schema updated successfully");
    }

    public static void injectDependencies() {
        Injector injector = Injector.getInstance();

        // Register the SessionFactory with the Injector
        EntityManagerFactory emf = sessionFactory;
        injector.register(EntityManagerFactory.class, emf);

        // Register the DataSeeder with the Injector
        injector.register(DataSeeder.class, new DataSeeder(emf));

        // Register the DAOs with their implementations
        BookingDAO bookingDAO = injector.getInstance(BookingDAOImpl.class);
        injector.register(BookingDAO.class, bookingDAO);

        CustomerDAO customerDAO = injector.getInstance(CustomerDAOImpl.class);
        injector.register(CustomerDAO.class, customerDAO);

        RestaurantDAO restaurantDAO = injector.getInstance(RestaurantDAOImpl.class);
        injector.register(RestaurantDAO.class, restaurantDAO);

        MenuDAO menuDAO = injector.getInstance(MenuDAOImpl.class);
        injector.register(MenuDAO.class, menuDAO);

        MenuItemDAO menuItemDAO = injector.getInstance(MenuItemDAOImpl.class);
        injector.register(MenuItemDAO.class, menuItemDAO);

        OrderDAO orderDAO = injector.getInstance(OrderDAOImpl.class);
        injector.register(OrderDAO.class, orderDAO);

        OrderItemDAO orderItemDAO = injector.getInstance(OrderItemDAOImpl.class);
        injector.register(OrderItemDAO.class, orderItemDAO);

        PaymentDAO paymentDAO = injector.getInstance(PaymentDAOImpl.class);
        injector.register(PaymentDAO.class, paymentDAO);

        RestaurantTableDAO restaurantTableDAO = injector.getInstance(RestaurantTableDAOImpl.class);
        injector.register(RestaurantTableDAO.class, restaurantTableDAO);

        ShipmentDAO shipmentDAO = injector.getInstance(ShipmentDAOImpl.class);
        injector.register(ShipmentDAO.class, shipmentDAO);

        UserDAO userDAO = injector.getInstance(UserDAOImpl.class);
        injector.register(UserDAO.class, userDAO);

        // Register the BookingController with its dependencies
        BookingController bookingController = injector.getInstance(BookingControllerImpl.class);
        injector.register(BookingController.class, bookingController);

        MenuController menuController = injector.getInstance(MenuControllerImpl.class);
        injector.register(MenuController.class, menuController);

        MenuItemController menuItemController = injector.getInstance(MenuItemControllerImpl.class);
        injector.register(MenuItemController.class, menuItemController);

        OrderController orderController = injector.getInstance(OrderControllerImpl.class);
        injector.register(OrderController.class, orderController);

        OrderItemController orderItemController = injector.getInstance(OrderItemControllerImpl.class);
        injector.register(OrderItemController.class, orderItemController);

        PaymentController paymentController = injector.getInstance(PaymentControllerImpl.class);
        injector.register(PaymentController.class, paymentController);

        RestaurantTableController restaurantTableController = injector.getInstance(RestaurantTableControllerImpl.class);
        injector.register(RestaurantTableController.class, restaurantTableController);

        ShipmentController shipmentController = injector.getInstance(ShipmentControllerImpl.class);
        injector.register(ShipmentController.class, shipmentController);

        UserController userController = injector.getInstance(UserControllerImpl.class);
        injector.register(UserController.class, userController);

        RestaurantController restaurantController = injector.getInstance(RestaurantControllerImpl.class);
        injector.register(RestaurantController.class, restaurantController);

        System.out.println("✔︎ Dependencies injected successfully");
    }
}
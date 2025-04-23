package com.restaurant;

import com.restaurant.config.Env;
import com.restaurant.controllers.BookingController;
import com.restaurant.controllers.impl.BookingControllerImpl;
import com.restaurant.daos.*;
import com.restaurant.daos.impl.*;
import com.restaurant.di.Injector;
import com.restaurant.models.*;
import com.restaurant.utils.DataSeeder;
import com.restaurant.views.Booking.BookingApp;
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

        BookingApp.launch();
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
                .addAnnotatedClass(MenuItemIngredient.class)
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(OrderItem.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Restaurant.class)
                .addAnnotatedClass(RestaurantTable.class)
                .addAnnotatedClass(Shipment.class)
                .addAnnotatedClass(Stock.class)
                .addAnnotatedClass(Supplier.class)
                .addAnnotatedClass(User.class)
                .buildMetadata();

        sessionFactory = metadata.buildSessionFactory();
        System.out.println("✔︎ Schema updated successfully");
    }

    public static void injectDependencies() {
        Injector injector = Injector.getInstance();

        EntityManagerFactory emf = sessionFactory;

        injector.register(EntityManagerFactory.class, emf);

        injector.register(DataSeeder.class, new DataSeeder(emf));

        BookingDAO bookingDAO = injector.getInstance(BookingDAOImpl.class);
        injector.register(BookingDAO.class, bookingDAO);

        MenuItemDAO menuItemDAO = injector.getInstance(MenuItemDAOImpl.class);
        injector.register(MenuItemDAO.class, menuItemDAO);

        OrderDAO orderDAO = injector.getInstance(OrderDAOImpl.class);
        injector.register(OrderDAO.class, orderDAO);

        PaymentDAO paymentDAO = injector.getInstance(PaymentDAOImpl.class);
        injector.register(PaymentDAO.class, paymentDAO);

        RestaurantTableDAO restaurantTableDAO = injector.getInstance(RestaurantTableDAOImpl.class);
        injector.register(RestaurantTableDAO.class, restaurantTableDAO);

        BookingController bookingController = new BookingControllerImpl(bookingDAO);
        injector.register(BookingController.class, bookingController);

        System.out.println("✔︎ Dependencies injected successfully");
    }
}
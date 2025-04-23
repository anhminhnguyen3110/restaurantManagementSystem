package com.restaurant.utils;

import com.restaurant.constants.*;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Injectable
public class DataSeeder {
    private final Random random = new Random();

    @Inject
    private final EntityManagerFactory emf;

    public DataSeeder(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void seed() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            List<Supplier> suppliers = seedSuppliers(em);
            List<Stock> stocks = seedStocks(em, suppliers);
            List<Restaurant> restaurants = seedRestaurants(em);
            List<Menu> menus = seedMenus(em, restaurants);
            List<MenuItem> menuItems = seedMenuItems(em, menus);
            seedMenuItemIngredients(em, menuItems, stocks);
            List<RestaurantTable> tables = seedTables(em, restaurants);

            List<User> users = seedUsers(em);
            List<Customer> customers = seedCustomers(em);

            List<Booking> bookings = seedBookings(em, customers, tables);
            List<Order> orders = seedOrders(em, tables, menuItems);

            seedPayments(em, orders);
            seedShipments(em, orders, users, customers);

            transaction.commit();
            System.out.println("✔︎ Database seeded successfully");
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw new RuntimeException("Seeding failed", e);
        }
    }

    private List<Supplier> seedSuppliers(EntityManager em) {
        List<Supplier> suppliers = new ArrayList<>();
        String[] names = {"Fresh Foods Co.", "Quality Meats Ltd.", "Dairy Direct", "Produce Partners", "Beverage World"};

        for (String name : names) {
            Supplier supplier = new Supplier();
            supplier.setName(name);
            supplier.setEmail(name.replaceAll("[^a-zA-Z]", "").toLowerCase() + "@example.com");
            supplier.setPhone(String.format("555-%04d", random.nextInt(10000)));
            supplier.setAddress("123 " + name + " Street");
            em.persist(supplier);
            suppliers.add(supplier);
        }
        return suppliers;
    }

    private List<Stock> seedStocks(EntityManager em, List<Supplier> suppliers) {
        List<Stock> stocks = new ArrayList<>();
        String[] items = {"Flour", "Sugar", "Butter", "Beef", "Chicken", "Milk", "Eggs", "Tomatoes", "Lettuce", "Coffee"};

        for (String item : items) {
            Stock stock = new Stock();
            stock.setName(item);
            stock.setQuantity(1000 + random.nextInt(5000));
            stock.setMinThreshold(500);
            stock.setSupplier(suppliers.get(random.nextInt(suppliers.size())));
            em.persist(stock);
            stocks.add(stock);
        }
        return stocks;
    }

    private List<Restaurant> seedRestaurants(EntityManager em) {
        List<Restaurant> restaurants = new ArrayList<>();
        String[] names = {
                "Downtown Bistro",
                "Harbor View Grill",
                "Mountain Peak Restaurant"
        };

        RestaurantStatus [] statuses = RestaurantStatus.values();

        for (int i = 0; i < names.length; i++) {
            Restaurant r = new Restaurant();
            r.setName(names[i]);
            r.setAddress("123 " + names[i] + " Street");
            r.setStatus(statuses[i]);
            em.persist(r);
            restaurants.add(r);
        }

        return restaurants;
    }

    private List<Menu> seedMenus(EntityManager em, List<Restaurant> restaurants) {
        List<Menu> menus = new ArrayList<>();
        String[] types = {"Main Menu", "Drinks Menu", "Dessert Menu"};

        for (Restaurant restaurant : restaurants) {
            for (String type : types) {
                Menu m = new Menu(type + " - " + restaurant.getName(), restaurant);
                em.persist(m);
                restaurant.addMenu(m);
                menus.add(m);
            }
        }
        return menus;
    }


    private List<MenuItem> seedMenuItems(EntityManager em, List<Menu> menus) {
        List<MenuItem> items = new ArrayList<>();
        String[] foodItems = {
                "Steak", "Salmon", "Caesar Salad", "Burger", "Pizza",
                "Pasta", "Ice Cream", "Coffee", "Wine", "Cheesecake"
        };

        for (Menu menu : menus) {
            for (String itemName : foodItems) {
                MenuItem mi = new MenuItem();
                mi.setName(itemName + " – " + menu.getName());
                mi.setDescription("Delicious " + itemName);
                mi.setPrice(5 + random.nextInt(30));
                mi.setAvailable(true);
                mi.setMenu(menu);
                em.persist(mi);

                menu.addItem(mi);
                items.add(mi);
            }
        }
        return items;
    }

    private void seedMenuItemIngredients(EntityManager em,
                                         List<MenuItem> items,
                                         List<Stock> stocks) {
        Random rnd = new Random();
        IngredientUnit[] units = IngredientUnit.values();

        for (MenuItem mi : items) {
            Collections.shuffle(stocks, rnd);
            int count = 2 + rnd.nextInt(3);
            for (int i = 0; i < count; i++) {
                Stock stock = stocks.get(i);

                MenuItemIngredient ingr = new MenuItemIngredient();
                ingr.setMenuItem(mi);
                ingr.setStock(stock);
                ingr.setQuantityRequired(50 + rnd.nextInt(150));
                IngredientUnit unit = units[rnd.nextInt(units.length)];
                ingr.setUnit(unit);
                em.persist(ingr);
            }
        }
    }

    private List<RestaurantTable> seedTables(EntityManager em, List<Restaurant> restaurants) {
        List<RestaurantTable> tables = new ArrayList<>();
        int[] capacities = {4, 8, 16};
        int tableNumber = 1;

        for (Restaurant restaurant : restaurants) {
            for (int i = 0; i < 100; i++) {
                RestaurantTable table = new RestaurantTable();
                table.setNumber(tableNumber++);
                table.setCapacity(capacities[random.nextInt(capacities.length)]);

                table.setX(i % 10);
                table.setY(i / 10);

                table.setAvailable(true);
                table.setRestaurant(restaurant);

                em.persist(table);
                tables.add(table);
                restaurant.getTables().add(table);
            }
        }
        return tables;
    }

    private List<User> seedUsers(EntityManager em) {
        List<User> users = new ArrayList<>();
        UserRole[] roles = UserRole.values();

        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPasswordHash("$2a$10$FAKEHASHFOREXAMPLEONLY");
            user.setRole(roles[random.nextInt(roles.length)]);
            user.setEmail("user" + i + "@example.com");
            user.setActive(true);
            em.persist(user);
            users.add(user);
        }
        return users;
    }

    private List<Customer> seedCustomers(EntityManager em) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Customer customer = new Customer();
            customer.setName("Customer " + i);
            customer.setPhoneNumber(String.format("555-%04d", random.nextInt(10000)));
            customer.setEmail("customer" + i + "@example.com");
            customer.setAddress("456 Customer Street #" + i);
            em.persist(customer);
            customers.add(customer);
        }
        return customers;
    }

    private List<Booking> seedBookings(EntityManager em, List<Customer> customers, List<RestaurantTable> tables) {
        List<Booking> bookings = new ArrayList<>();
        BookingStatus[] statuses = BookingStatus.values();
        BookingDuration[] durations = BookingDuration.values();

        for (int i = 0; i < 50; i++) {
            Booking booking = new Booking();
            booking.setStart(LocalDateTime.now().plusHours(random.nextInt(168)));
            booking.setDuration(durations[random.nextInt(durations.length)]);
            booking.setTable(tables.get(random.nextInt(tables.size())));
            booking.setCustomer(customers.get(random.nextInt(customers.size())));
            booking.setStatus(statuses[random.nextInt(statuses.length)]);
            em.persist(booking);
            bookings.add(booking);
        }
        return bookings;
    }

    private List<Order> seedOrders(EntityManager em, List<RestaurantTable> tables, List<MenuItem> menuItems) {
        List<Order> orders = new ArrayList<>();
        OrderType[] types = OrderType.values();
        OrderItemStatus[] orderItemStatuses = OrderItemStatus.values();
        OrderStatus[] orderStatuses = OrderStatus.values();

        Random rnd = new Random();

        for (RestaurantTable table : tables) {
            int ordersPerTable = 1 + rnd.nextInt(2);
            for (int j = 0; j < ordersPerTable; j++) {
                Order o = new Order();
                o.setRestaurantTable(table);
                o.setOrderType(types[rnd.nextInt(types.length)]);
                OrderStatus os = orderStatuses[rnd.nextInt(orderStatuses.length)];
                o.setStatus(os);

                int itemCount = 2 + rnd.nextInt(4);
                for (int k = 0; k < itemCount; k++) {
                    MenuItem mi = menuItems.get(rnd.nextInt(menuItems.size()));
                    OrderItem oi = new OrderItem();
                    oi.setMenuItem(mi);
                    oi.setQuantity(1 + rnd.nextInt(3));

                    if(os == OrderStatus.COMPLETED) {
                        oi.setStatus(OrderItemStatus.SERVED);
                    }
                    else {
                        oi.setStatus(orderItemStatuses[rnd.nextInt(orderItemStatuses.length)]);
                    }
                    o.addItem(oi);
                }

                em.persist(o);
                orders.add(o);
            }
        }
        return orders;
    }

    private void seedPayments(EntityManager em, List<Order> orders) {
        PaymentMethod[] methods = PaymentMethod.values();
        PaymentStatus[] statuses = PaymentStatus.values();

        for (Order order : orders) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setUserPayAmount(order.getTotalPrice() + random.nextInt(20));
            payment.setChangeAmount(payment.getUserPayAmount() - order.getTotalPrice());
            payment.setMethod(methods[random.nextInt(methods.length)]);
            payment.setStatus(statuses[random.nextInt(statuses.length)]);
            em.persist(payment);
            order.setPayment(payment);
        }
    }

    private void seedShipments(EntityManager em, List<Order> orders,
                               List<User> users, List<Customer> customers) {
        List<User> shippers = users.stream()
                .filter(u -> u.getRole() == UserRole.SHIPPER)
                .toList();

        ShipmentStatus[] statuses = ShipmentStatus.values();
        ShipmentService[] services = ShipmentService.values();

        for (Order order : orders) {
            ShipmentStatus status = statuses[random.nextInt(statuses.length)];
            ShipmentService service = services[random.nextInt(services.length)];

            if (status == ShipmentStatus.SUCCESS) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.PENDING);
            }

            if (order.getOrderType() == OrderType.DELIVERY) {
                Shipment shipment = new Shipment();
                shipment.setOrder(order);
                shipment.setServiceType(service);
                shipment.setStatus(status);
                shipment.setCustomer(customers.get(random.nextInt(customers.size())));
                if(service == ShipmentService.INTERNAL) {
                    shipment.setShipper(shippers.get(random.nextInt(shippers.size())));
                }
                em.persist(shipment);
                order.setShipment(shipment);
            }
        }
    }
}
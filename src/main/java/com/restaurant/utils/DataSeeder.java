package com.restaurant.utils;

import com.restaurant.constants.*;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
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
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                List<Restaurant> restaurants = seedRestaurants(em);
                List<Menu> menus = seedMenus(em, restaurants);
                List<MenuItem> menuItems = seedMenuItems(em, menus);
                List<RestaurantTable> tables = seedTables(em, restaurants);
                List<User> users = seedUsers(em);
                List<Customer> customers = seedCustomers(em);
                seedBookings(em, customers, tables);
                List<Order> orders = seedOrders(em, tables, menuItems);
                seedPayments(em, orders);
                seedShipments(em, orders, users, customers);

                tx.commit();
                System.out.println("✔︎ Database seeded successfully");
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw new RuntimeException("Seeding failed", e);
            }
        }
    }

    private List<Restaurant> seedRestaurants(EntityManager em) {
        List<Restaurant> restaurants = new ArrayList<>();
        String[] names = {
                "Downtown Bistro",
                "Harbor View Grill",
                "Mountain Peak Restaurant"
        };
        RestaurantStatus[] statuses = RestaurantStatus.values();
        int defaultMaxX = 10, defaultMaxY = 10;
        for (int i = 0; i < names.length; i++) {
            Restaurant r = new Restaurant();
            r.setName(names[i]);
            r.setAddress("123 " + names[i] + " Street");
            r.setStatus(statuses[i % statuses.length]);
            r.setMaxX(defaultMaxX);
            r.setMaxY(defaultMaxY);
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
                Menu m = new Menu();
                m.setName(type + " - " + restaurant.getName());
                m.setRestaurant(restaurant);
                em.persist(m);
                restaurant.getMenus().add(m);
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
                mi.setMenu(menu);
                em.persist(mi);
                menu.getItems().add(mi);
                items.add(mi);
            }
        }
        return items;
    }

    private List<RestaurantTable> seedTables(EntityManager em, List<Restaurant> restaurants) {
        List<RestaurantTable> tables = new ArrayList<>();
        int[] capacities = {4, 8, 16};
        for (Restaurant restaurant : restaurants) {
            int maxX = restaurant.getMaxX();
            int maxY = restaurant.getMaxY();
            boolean[][] occupied = new boolean[maxY][maxX];
            for (int tableNumber = 1; tableNumber <= 10; tableNumber++) {
                int startX, startY, endX, endY;
                do {
                    startX = random.nextInt(maxX);
                    startY = random.nextInt(maxY);
                    int w = 1 + random.nextInt(3);
                    int h = 1 + random.nextInt(3);
                    endX = Math.min(maxX - 1, startX + w - 1);
                    endY = Math.min(maxY - 1, startY + h - 1);
                } while (regionOverlaps(occupied, startX, startY, endX, endY));
                for (int y = startY; y <= endY; y++) {
                    for (int x = startX; x <= endX; x++) {
                        occupied[y][x] = true;
                    }
                }
                RestaurantTable table = new RestaurantTable();
                table.setRestaurant(restaurant);
                table.setNumber(tableNumber);
                table.setCapacity(capacities[random.nextInt(capacities.length)]);
                table.setStartX(startX);
                table.setStartY(startY);
                table.setEndX(endX);
                table.setEndY(endY);
                table.setAvailable(true);
                em.persist(table);
                restaurant.getTables().add(table);
                tables.add(table);
            }
        }
        return tables;
    }

    private boolean regionOverlaps(boolean[][] occ, int sx, int sy, int ex, int ey) {
        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                if (occ[y][x]) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<User> seedUsers(EntityManager em) {
        List<User> users = new ArrayList<>();
        User owner = new User();
        owner.setName("Owner");
        owner.setUsername("admin");
        owner.setPasswordHash(BCrypt.hashpw("123", BCrypt.gensalt()));
        owner.setRole(UserRole.OWNER);
        owner.setEmail("owner@example.com");
        owner.setActive(true);
        em.persist(owner);
        users.add(owner);
        List<UserRole> nonOwnerRoles = new ArrayList<>();
        for (UserRole r : UserRole.values()) {
            if (r != UserRole.OWNER) {
                nonOwnerRoles.add(r);
            }
        }
        for (int i = 1; i <= 9; i++) {
            User u = new User();
            u.setName("User " + i);
            u.setUsername("user" + i);
            u.setPasswordHash(BCrypt.hashpw("123", BCrypt.gensalt()));
            u.setRole(nonOwnerRoles.get(random.nextInt(nonOwnerRoles.size())));
            u.setEmail("user" + i + "@example.com");
            u.setActive(true);
            em.persist(u);
            users.add(u);
        }
        return users;
    }

    private List<Customer> seedCustomers(EntityManager em) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer();
            c.setName("Customer " + i);
            c.setPhoneNumber(String.format("555-%04d", random.nextInt(10000)));
            c.setEmail("customer" + i + "@example.com");
            c.setAddress("456 Customer Street #" + i);
            em.persist(c);
            customers.add(c);
        }
        return customers;
    }

    private void seedBookings(EntityManager em, List<Customer> customers, List<RestaurantTable> tables) {
        BookingStatus[] statuses = BookingStatus.values();
        BookingTimeSlot[] slots = BookingTimeSlot.values();
        for (int i = 0; i < 50; i++) {
            Booking b = new Booking();
            LocalDate date = LocalDate.now().plusDays(1 + random.nextInt(14));
            b.setDate(date);
            int startIdx = random.nextInt(slots.length - 1);
            int maxOffset = slots.length - startIdx - 1;
            int offset = 1 + random.nextInt(maxOffset);
            b.setStartTime(slots[startIdx]);
            b.setEndTime(slots[startIdx + offset]);
            b.setTable(tables.get(random.nextInt(tables.size())));
            b.setCustomer(customers.get(random.nextInt(customers.size())));
            b.setStatus(statuses[random.nextInt(statuses.length)]);
            em.persist(b);
        }
    }

    private List<Order> seedOrders(EntityManager em, List<RestaurantTable> tables, List<MenuItem> menuItems) {
        List<Order> orders = new ArrayList<>();
        OrderType[] types = OrderType.values();
        OrderItemStatus[] oiStatus = OrderItemStatus.values();
        OrderStatus[] oStatus = OrderStatus.values();
        for (RestaurantTable table : tables) {
            int perTable = 1 + random.nextInt(2);
            for (int j = 0; j < perTable; j++) {
                Order o = new Order();
                o.setOrderType(types[random.nextInt(types.length)]);
                if (o.getOrderType() == OrderType.DINE_IN) {
                    continue;
                } else {
                    o.setRestaurant(table.getRestaurant());
                }
                OrderStatus os = oStatus[random.nextInt(oStatus.length)];
                o.setStatus(os);
                int itemCount = 2 + random.nextInt(4);
                for (int k = 0; k < itemCount; k++) {
                    MenuItem mi = menuItems.get(random.nextInt(menuItems.size()));
                    OrderItem oi = new OrderItem();
                    oi.setMenuItem(mi);
                    oi.setQuantity(1 + random.nextInt(3));
                    oi.setStatus(os == OrderStatus.COMPLETED
                            ? OrderItemStatus.SERVED
                            : oiStatus[random.nextInt(oiStatus.length)]);
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
            Payment p = new Payment();
            p.setOrder(order);
            p.setUserPayAmount(order.getTotalPrice() + random.nextInt(20));
            p.setChangeAmount(p.getUserPayAmount() - order.getTotalPrice());
            p.setMethod(methods[random.nextInt(methods.length)]);
            p.setStatus(statuses[random.nextInt(statuses.length)]);
            em.persist(p);
            order.setPayment(p);
        }
    }

    private void seedShipments(EntityManager em, List<Order> orders, List<User> users, List<Customer> customers) {
        List<User> shippers = new ArrayList<>();
        for (User u : users) {
            if (u.getRole() == UserRole.SHIPPER) {
                shippers.add(u);
            }
        }
        ShipmentStatus[] statuses = ShipmentStatus.values();
        ShipmentService[] services = ShipmentService.values();
        for (Order order : orders) {
            ShipmentStatus ss = statuses[random.nextInt(statuses.length)];
            ShipmentService svc = services[random.nextInt(services.length)];
            if (ss == ShipmentStatus.SUCCESS) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.PENDING);
            }
            if (order.getOrderType() == OrderType.DELIVERY) {
                Shipment s = new Shipment();
                s.setOrder(order);
                s.setServiceType(svc);
                s.setStatus(ss);
                s.setCustomer(customers.get(random.nextInt(customers.size())));
                if (svc == ShipmentService.INTERNAL && !shippers.isEmpty()) {
                    s.setShipper(shippers.get(random.nextInt(shippers.size())));
                }
                em.persist(s);
                order.setShipment(s);
            }
        }
    }
}

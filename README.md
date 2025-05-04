# Restaurant Management System

## Getting Started

Follow these steps to set up, build, test, and run the application.

### Prerequisites

- Docker & Docker Compose
- Java 17 or higher
- Apache Maven 3.6+

### 1. Start the database

```bash
# Launch MySQL in Docker
docker-compose up -d
```

### 2. Build

```bash
# Download dependencies and compile code
mvn clean install
```

### 3. Run the application

```bash
# Launch the Main class directly via Maven
mvn exec:java -Dexec.mainClass="com.restaurant.Main"

# Or build a runnable jar and run it
mvn package
java -jar target/*.jar
```

---

## Table of Contents

- [Getting Started](#getting-started)
- [Clean Code Principles](#clean-code-principles)
- [Architecture: MVC](#architecture-mvc)
- [Directory Structure](#directory-structure)
- [Design Patterns](#design-patterns)
- [UML Class Diagram](#uml-class-diagram)
- [UI Screenshots](#ui-screenshots)
- [User Authentication & Permissions](#user-authentication--permissions)
- [Out of scope](#out-of-scope)
- [Testing](#testing)
- [Table Map View](#table-map-view)

---

## Clean Code Principles

- **Meaningful Names**: Classes, methods, and variables are named descriptively.
- **Single Responsibility**: Each class or module has one responsibility.
- **Open/Closed**: The code is open for extension but closed for modification.
- **DRY (Don't Repeat Yourself)**: Shared logic is abstracted and reused.
- **Dependency Injection**: Dependencies are injected, reducing coupling.
- **Error Handling**: Centralized via a Pub/Sub Service for user-friendly error messages.

---

## Architecture: MVC
### Detailed Class Diagram for User Model
![](UML%20diagram/diagram.jpg)
The application is organized into three primary layers:

1. **Model** (`com.restaurant.models`)
   - Represents domain entities (e.g., `User`, `Restaurant`, `Order`).
   - Annotated with JPA (`@Entity`) for persistence.

2. **View** (`com.restaurant.views`)
   - Swing-based UI components (e.g., `MainView`, `OrderListView`).
   - Responsible for rendering data and capturing user events.

3. **Controller** (`com.restaurant.controllers` & `impl`)
   - Interfaces define operations (e.g., `OrderController`).
   - Implementations (`*ControllerImpl`) handle business logic and delegate to DAOs.

Data access is encapsulated in **DAO** classes (`com.restaurant.daos.impl`) using Hibernate/JPA.

---

## Design Patterns

The system uses several classic object-oriented design patterns to promote modularity, testability, and maintainability:

### MVC (Model-View-Controller)

- **Model**: Domain entities such as `User`, `Restaurant`, `Order`, annotated with JPA.
- **View**: Swing UI components for interacting with users.
- **Controller**: Interfaces and implementations that handle application logic.

### DAO (Data Access Object)

- Encapsulates database interactions behind interfaces.
- Implemented with JPA/Hibernate in the `daos.impl` package.

### Singleton

- **Used in**: `ErrorPubSubService`, `BookingInputValidator`, `MenuInputValidator`, and other input validators.
- **Purpose**: Ensures a single shared instance for components like the error event system or validators, avoiding redundant instantiations.

```java
public static ErrorPubSubService getInstance() {
    if (instance == null) {
        instance = new ErrorPubSubService();
    }
    return instance;
}
```

### Factory

- **Used in**: `ValidatorFactory`
- **Purpose**: Provides a centralized factory for retrieving the correct validator instance based on DTO type. Simplifies dependency wiring.

```java
public static <C, U> Validator<C, U> getCreateValidator(Class<C> cls) {
    return (Validator<C, U>) CREATE_REG.get(cls);
}
```

### Observer (Publish/Subscribe)

- **Used in**: `ErrorPubSubService`
- **Purpose**: UI components subscribe to error events to handle user-friendly error messages when validation or system errors occur.

```java
@Override
public <T> void subscribe(Class<T> eventType, Consumer<T> consumer) {
    subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(consumer);
}
```

When validation fails:
```java
pub.publish(new ErrorEvent("Validation errors:\n..."));
```

These patterns work together to decouple components, facilitate testability, and enhance maintainability in a growing Java Swing-based system.

---

## Directory Structure

```
src/main/java/com/restaurant/
├── config/            # Environment & Hibernate setup
├── constants/         # Enum and constant classes
├── controllers/       # Controller interfaces
├── controllers/impl/  # Controller implementations
├── daos/              # DAO interfaces
├── daos/impl/         # DAO implementations (Hibernate)
├── di/                # Dependency Injection annotations & container
├── dtos/              # Data Transfer Objects
├── events/            # Error events
├── models/            # JPA entities
├── pubsub/            # Pub/Sub services
├── seed/              # Initial data seeding
├── validators/        # Input validators and factories
└── views/             # Swing UI components
```

---

## Design Patterns

- **MVC**: Separates presentation (View), business logic (Controller), and data (Model).
- **DAO**: Encapsulates database operations behind interfaces.
- **Singleton**: `Injector`, `ErrorPubSubService` ensure single instances.
- **Factory**: `ValidatorFactory` builds validators; DI container binds dependencies.
- **Observer (Pub/Sub)**: Error events are published and subscribed to in UI.
- **Dependency Injection**: `Injector` registers and injects dependencies at startup.

---

## UML Class Diagram

- ![Models](UML%20diagram/models.jpg)
- ![Database](UML%20diagram/database.png)
- ![Testing Coverage](UML%20diagram/testingCoverage.png)

---

## UI Screenshots

| Feature             | Screenshot |
|---------------------|------------|
| Login               | ![](UML%20diagram/UI/Login.png) |
| Add User            | ![](UML%20diagram/UI/AddUser.png) |
| Edit User           | ![](UML%20diagram/UI/EditUser.png) |
| User Filter         | ![](UML%20diagram/UI/UserFilterBar.png) |
| User Pagination     | ![](UML%20diagram/UI/UserPagination.png) |
| Add Booking         | ![](UML%20diagram/UI/AddBooking.png) |
| Edit Booking        | ![](UML%20diagram/UI/EditBooking.png) |
| Booking Filter      | ![](UML%20diagram/UI/BookingFilterBar.png) |
| Booked Table on Map | ![](UML%20diagram/UI/BookedTableOnMap.png) |
| Table Dialog (Before)| ![](UML%20diagram/UI/TableDialogBeforeEditing.png) |
| Table Dialog (After)| ![](UML%20diagram/UI/TableDialogAfterEditing.png) |
| New Table Added     | ![](UML%20diagram/UI/NewTableIsAdded.png) |
| Table Map View      | ![](UML%20diagram/UI/TableMapView.png) |
| Table Map View (After Edit) | ![](UML%20diagram/UI/TableMapViewAfterEditing.png) |
| Add Restaurant      | ![](UML%20diagram/UI/AddRestaurant.png) |
| Edit Restaurant     | ![](UML%20diagram/UI/EditRestaurant.png) |
| Restaurant Filter   | ![](UML%20diagram/UI/RestaurantFilterBar.png) |
| Restaurant Pagination | ![](UML%20diagram/UI/RestaurantPagination.png) |
| Add Menu            | ![](UML%20diagram/UI/AddMenu.png) |
| Edit Menu           | ![](UML%20diagram/UI/EditMenu.png) |
| Menu Detail         | ![](UML%20diagram/UI/MenuDetail.png) |
| Menu Filter         | ![](UML%20diagram/UI/MenuFilterBar.png) |
| Add Menu Item       | ![](UML%20diagram/UI/AddMenuItem.png) |
| Edit Menu Item      | ![](UML%20diagram/UI/EditMenuItem.png) |
| Menu Item Filter    | ![](UML%20diagram/UI/MenuItemFilterBar.png) |
| Menu Item Pagination| ![](UML%20diagram/UI/MenuItemPagination.png) |
| Add Order (Dine-In) | ![](UML%20diagram/UI/AddDineinOrder.png) |
| Add Order (Delivery)| ![](UML%20diagram/UI/AddDlliveryOrder.png) |
| Edit Order Item     | ![](UML%20diagram/UI/EditOrderItem.png) |
| Order Filter        | ![](UML%20diagram/UI/OrderFilterBar.png) |
| Order Pagination    | ![](UML%20diagram/UI/OrderPagination.png) |
| Update Order Detail | ![](UML%20diagram/UI/UpdateOrderDetail.png) |
| Add Order Item      | ![](UML%20diagram/UI/AddOrderItem.png) |
| Order Item Filter   | ![](UML%20diagram/UI/OrderItemFilterBar.png) |
| Order Item Pagination | ![](UML%20diagram/UI/OrderItemPagination.png) |
| Add Payment         | ![](UML%20diagram/UI/AddPayment.png) |
| Edit Payment        | ![](UML%20diagram/UI/EditPayment.png) |
| Payment Filter      | ![](UML%20diagram/UI/PaymentFilterBar.png) |
| Payment Pagination  | ![](UML%20diagram/UI/PaymentPagination.png) |
| Add Shipment        | ![](UML%20diagram/UI/AddShipment.png) |
| Edit Shipment       | ![](UML%20diagram/UI/EditShipment.png) |
| Shipment Filter     | ![](UML%20diagram/UI/ShipmentFilterBar.png) |
| Shipment Pagination | ![](UML%20diagram/UI/ShipmentPagination.png) |

---

## User Authentication & Permissions

### Credentials

| Role         | Username  | Password |
|--------------|-----------|----------|
| Owner        | admin     | 123      |
| Manager      | manager   | 123      |
| Wait Staff   | waitstaff | 123      |
| Cook         | cook      | 123      |
| Shipper      | shipper   | 123      |

### Tab Access

| Tab             | Owner | Manager | Wait Staff | Cook | Shipper |
|------------------|:-----:|:-------:|:----------:|:----:|:--------:|
| **Order Items**  |  ✓   |    ✓    |     ✓      |  ✓  |    ✗     |
| **Shipments**    |  ✓   |    ✓    |     ✓      |  ✗  |    ✓     |
| **Menus**        |  ✓   |    ✓    |     ✓      |  ✗  |    ✗     |
| **Menu Items**   |  ✓   |    ✓    |     ✗      |  ✓  |    ✗     |
| **Orders**       |  ✓   |    ✓    |     ✓      |  ✗  |    ✗     |
| **Payments**     |  ✓   |    ✓    |     ✓      |  ✗  |    ✗     |
| **Tables**       |  ✓   |    ✓    |     ✓      |  ✗  |    ✗     |
| **Bookings**     |  ✓   |    ✓    |     ✓      |  ✗  |    ✗     |
| **Restaurants**  |  ✓   |    ✓    |     ✗      |  ✗  |    ✗     |
| **Users**        |  ✓   |    ✗    |     ✗      |  ✗  |    ✗     |

---

## Out of scope

- Real payment service
- Real shipment service
- Stock & Supplier management

---

## Testing

Except for UI, Main and Seed, everything is provided with unit testing.

## Table Map View

In the table map view:
- Red tables indicate not available
- Green tables indicate available

---

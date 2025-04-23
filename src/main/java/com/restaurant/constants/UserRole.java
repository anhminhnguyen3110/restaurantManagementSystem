package com.restaurant.constants;

public enum UserRole {
    STAFF("staff"),
    MANAGER("manager"),
    OWNER("owner"),
    SHIPPER("shipper");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getRole();
    }

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.role.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("No constant with role " + role + " found");
    }
}

package com.restaurant.constants;

public enum UserRole {
    STAFF("Staff"),
    MANAGER("Manager"),
    OWNER("Owner");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
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

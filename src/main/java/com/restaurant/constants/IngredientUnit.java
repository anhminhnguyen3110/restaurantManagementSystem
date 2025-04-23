package com.restaurant.constants;

public enum IngredientUnit {
    GRAMS("grams"),
    KILOGRAMS("kilograms"),
    LITERS("liters"),
    PIECES("pieces"),
    CUPS("cups"),
    TABLESPOONS("tablespoons");

    private final String unit;

    IngredientUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit.toLowerCase().replace("_", " ");
    }

    public static IngredientUnit fromString(String unit) {
        for (IngredientUnit ingredientUnit : IngredientUnit.values()) {
            if (ingredientUnit.unit.equalsIgnoreCase(unit)) {
                return ingredientUnit;
            }
        }
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }

    @Override
    public String toString() {
        return this.getUnit();
    }
}

package com.restaurant.constants;

import java.time.Duration;
import java.time.LocalDateTime;

public enum BookingDuration {

    HALF_HOUR(30),
    ONE_HOUR(60),
    ONE_AND_HALF_HOUR(90),
    TWO_HOURS(120),
    TWO_AND_HALF_HOUR(150),
    THREE_HOURS(180),
    THREE_AND_HALF_HOUR(210),
    FOUR_HOURS(240),
    FOUR_AND_HALF_HOUR(270),
    FIVE_HOURS(300);

    private final int minutes;

    BookingDuration(int m) {
        this.minutes = m;
    }

    public int getMinutes() {
        return minutes;
    }

    public Duration toDuration() {
        return Duration.ofMinutes(minutes);
    }

    public LocalDateTime addTo(LocalDateTime start) {
        return start.plus(toDuration());
    }

    public static BookingDuration fromMinutes(int m) {
        for (BookingDuration d : values()) if (d.minutes == m) return d;
        throw new IllegalArgumentException("Unsupported duration " + m + " min");
    }

    @Override
    public String toString() {
        int h = minutes / 60;
        int m = minutes % 60;
        if (h == 0) return m + " min";
        if (m == 0) return h + " h";
        return h + " h " + m + " min";
    }
}
package com.restaurant;

import com.restaurant.config.Env;

public class Main {
    public static void main(String[] args) {
        System.out.println("DB_URL: " + Env.getInstance().get("DB_URL"));
    }
}
package com.restaurant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public final class DatabaseConfig {

    private static volatile DatabaseConfig INSTANCE;
    private final HikariDataSource dataSource;

    private DatabaseConfig() {
        this.dataSource = initDataSource();
    }

    public static DatabaseConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (DatabaseConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseConfig();
                }
            }
        }
        return INSTANCE;
    }

    private HikariDataSource initDataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(Env.getInstance().get("DB_URL", "jdbc:mysql://localhost:3306/restaurant"));
        cfg.setUsername(Env.getInstance().get("DB_USER", "root"));
        cfg.setPassword(Env.getInstance().get("DB_PASS", "123456"));
        cfg.setMaximumPoolSize(Integer.parseInt(Env.getInstance().get("DB_POOL_SIZE", "10")));
        return new HikariDataSource(cfg);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        dataSource.close();
    }
}

package com.fighton;

public class Constants {
    private static final boolean devMode = true;

    // SQL setup
    static final String JDBC_DRIVER = devMode ? "com.mysql.cj.jdbc.Driver" : "";
    static final String DB_URL = devMode ? "jdbc:mysql://localhost/fighton?useLegacyDatetimeCode=false&serverTimezone=UTC" : "";

    static final String USER = devMode ? "root" : "";
    static final String PASSWORD = devMode ? "worldofwar1" : "";
}

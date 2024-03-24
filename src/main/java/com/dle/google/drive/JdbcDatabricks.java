package com.dle.google.drive;

import java.sql.*;
import java.util.Properties;

public class JdbcDatabricks {
    public static void main(String[] args) {
        String url = "jdbc:databricks://adb-5541770754405938.18.azuredatabricks.net:443";
        Properties p = new java.util.Properties();
        p.put("httpPath", "/sql/1.0/warehouses/b8ccc44142b335f0");
        p.put("AuthMech", "3");
        p.put("UID", "token");
        p.put("PWD", "");
        p.put("EnableArrow", "0"); //https://community.databricks.com/t5/data-engineering/jdbc-driver-support-for-openjdk-17/td-p/32584
        try {
            Connection conn = DriverManager.getConnection(url, p);
            String query = "select * from samples.tpch.customer limit 5";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String c_name = rs.getString("c_name");
                    String c_address = rs.getString("c_address");
                    String c_comment = rs.getString("c_comment");
                    System.out.printf("[c_name: %s, c_address: %s, c_comment: %s]\n", c_name, c_address, c_comment);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

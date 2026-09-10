package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DriverManager 로 연결을 100번 만들고 끊는 데 걸리는 시간 측정
public class ConnectionCostTest {
    public static void main(String[] args) throws SQLException {
        String url  = "jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Seoul";
        String user = System.getenv("DB_USER");
        String pwd  = System.getenv("DB_PASSWORD");

        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
                // 아무 SQL 도 실행하지 않습니다. 연결과 종료 비용만 측정합니다.
            }
        }
        long end = System.nanoTime();

        System.out.println("DriverManager 100회 연결/종료: " + (end - start) / 1_000_000 + " ms");
    }
}
package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DriverManager 로 연결을 100번 만들고 끊는 데 걸리는 시간 측정
public class PoolCostTest {
    public static void main(String[] args) throws SQLException {

        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                // 아무 SQL 도 실행하지 않습니다. 연결과 종료 비용만 측정합니다.
            }
        }
        long end = System.nanoTime();

        System.out.println("DriverManager 100회 연결/종료: " + (end - start) / 1_000_000 + " ms");
    }
}
package org.example.z_study.dao;

import org.example.util.DatabaseUtil;
import org.example.z_study.dto.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 관리자 관련 SQL을 실행하는 DAO 클래스
public class AdminDAO {

    // 관리자 ID로 관리자 한 명을 조회

    // 비밀번호는 SQL에서 비교하지 않고 DB에 저장된 값을 그대로 가져오게 함
    // "입력한 비밀번호가 맞는가"는 업무 규칙이므로 Service에게 판단을 시킬 예정이다.
    // 이렇게하면 추후 나중에 비밀번호 암호화 (해시처리) 바꿀 때 크게 변경할 부분이 없어짐
    public Admin findByAdminId (String adminId) {

        String sql = """
                SELECT id, admin_id, password, name
                FROM admins
                WHERE admin_id = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, adminId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return Admin.builder()
                            .id(rs.getInt("id"))
                            .adminId(rs.getString("admin_id"))
                            .password(rs.getString("password"))
                            .name(rs.getString("name"))
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

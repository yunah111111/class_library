package org.example.z_study.dao;

import org.example.z_study.dto.Student;
import org.example.z_study.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // 학생 등록 기능
    public int addStudent(Student student) {

        int rows = 0;
        String sql = """
                INSERT TO students VALUE(?, ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());

                rows = pstmt.executeUpdate();

                System.out.println(rows + "행이 추가되었습니다.");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    // 학생 전체 조회 기능
    public List<Student> getAllStudent() {
        List<Student> studentList = new ArrayList<>();

        String sql = """
                SELECT * FROM students ORDER BY id
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    studentList.add(createStudent(rs));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return studentList;
    }


    // 학번으로 학생 조회 기능 --> 로그인
    public Student getStudentByStudentId(String studentId) {
        Student student = new Student();

        String sql = """
                SELECT * FROM student WHERE student_id = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return createStudent(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;

    }


    private Student createStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setStudentId(rs.getString("student_id"));

        return student;
    }
}

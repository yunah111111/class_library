package org.example.dao;

import org.example.dto.Student;
import org.example.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Todo - 추후 사용하는 측 확인해서 리턴 타입 결정
    // 학생 등록 기능
    // 테이블 기준으로 컬럼명 작성
    public int addStudent(Student student) {
        int rows = 0;
        String sql = """
                INSERT INTO students(name, student_id)
                VALUES (?, ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());
                // INSERT, UPDATE, DELETE에 사용해야 함
                rows = pstmt.executeUpdate();
                System.out.println(rows + "행이 추가 되었습니다.");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    // 학생 전체 조회 기능
    public List<Student> getAllStudent() {

        // SELECT * FROM students;
        List<Student> studentList = new ArrayList<>();
        // 조회한 학생 객체들을 저장할 리스트 생성
        // 나중에 이 리스트를 return해서 전체 학생 정보를 전달함


        // 1. 커넥션 객체 생성
        // 2. 사용할 SQL 구문 준비
        // 3. 쿼리를 실행할 PreparedStatement 준비, 값 바인딩
        // 4. 쿼리 실행 결과집합 받기

        String sql = """
                SELECT * FROM students ORDER BY id;
                """;
        // id를 기준으로 오름차순 정렬

        try (Connection conn = DatabaseUtil.getConnection()) {
            // DB 연결 객체 생성

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // 위에서 작성한 SQL문을 실행할 PReparedStatement 생성

                ResultSet rs = pstmt.executeQuery();
                // SELECT문 실행
                // 조회 결과를 ResultSet에 저장

                while(rs.next()) {
                    // ResultSet에서 다음 데이터가 있는 동안 반복
                    // 자료구조에 생성된 Student 객체를 하나씩 추가 함.
                    studentList.add(createStudent(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return studentList;
    }

    // 학번으로 학생 조회 기능 --> 로그인
    public Student getStudentByStudentId (String studentId) {
        String sql = """
                SELECT * FROM students WHERE student_id = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, studentId);

            ResultSet rs = pstmt.executeQuery();
            // 굳이 결과집합이 단일행이라면 while행 실행할 필요 없다.
            if (rs.next()) {
                return createStudent(rs);
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

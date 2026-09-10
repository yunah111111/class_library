package org.example.z_study.dao;

import org.example.dto.Borrow;
import org.example.util.DatabaseUtil;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // 현재 대출 중인 도서 목록 조회
    // 사용자에게 보여주려면 도서 제목과 학생 이름이 필요함
    // books, student 테이블에서 JOIN하여 한번에 가져와야함
    public List<Borrow> getBorrowedBooks() {
        List<Borrow> borrowList = new ArrayList<>();

        String sql = """
                SELECT b.id, b.book_id, bk.title, b.student_id, s.name, b.borrow_date
                FROM borrows b
                INNER JOIN books bk ON b.book_id = bk.id
                INNER JOIN students s ON b.student_id = s.id
                WHERE b.return_date IS NULL
                ORDER BY b.borrow_date ASC;
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Borrow borrow = new Borrow();
                    borrowList.add(Borrow.builder()
                                    .id(rs.getInt("id"))
                                    .bookId(rs.getInt("book_id"))
                                    .bookTitle(rs.getString("book_title"))
                                    .studentId(rs.getInt("student_id"))
                                    .studentName(rs.getString("name"))
                                    .borrowDate(rs.getDate("borrow_date").toLocalDate())
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return borrowList;
    }

    // 2. 도서 대출 기능 (트랜잭션)
    // [처리 순서]
    // 1. DB 연결을 얻고 자동 커밋을 끈다. (트랜잭션 시작)
    // 2. 도서가 존재하고 대출 가능한 상태인지 확인 -- SELECT
    // 3. borrows 테이블에 대출 기록 -- INSERT
    // 4. books 테이블에 available을 False로 변경 -- UPDATE
    // 5. 2 ~ 4가 모두 성공하면 commit, 하나라도 실패하면 rollback
    // 6. 자동 커밋을 원래대로 되돌리고 연결을 닫는다.

    public void borrowBook(int bookId, int studentId) throws SQLException {

        Connection conn = null;
        // try-with-resources로 선언하지 않은 이유
        // catch 블록에서 rollback을 호출하려면 conn 변수가 catch 안에서도 보여야 함
        // 그래서 try 바같에 선언하고 finally에서 직접 닫음

        try{

            // 1. 트랜잭션 시작
            conn = DatabaseUtil.getConnection();
            // 기본값 autoCommit - true이고 이 상태에서는 SQL 한줄 한줄마다 즉시 확정(반영)이 됨
            // 이 값 false로 변경하면 commit()을 호출하기 전까지 모든 변경 사항이 임시 상태로 남음
            conn.setAutoCommit(false);

            // 2. 대출 가능 여부 확인
            String checkSql = """
                    SELECT availabe FROM books WHERE id = ?
                    """;

            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("존재하지 않는 도서입니다. ID: " + bookId);
                    }
                    if (!rs.getBoolean("available")) {
                        throw new SQLException("현재 대출 중인 도서입니다. 반납 후 이용 가능합니다.")
                    }
                }
            }

            // 3. 코드가 여기가지 내려온다면 대출 가능한 bookId이다. --> 대출 기록 처리
            String borrowSql = """
                    INSERT INTO borrows (book_id, student_id, borrow_date)
                    VALUES (?, ?, ?)
                    """;

            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
                // java.time.Localdate를 JDBC가 이해하는 java.sql.Date로 변환해주어야 한다.
                borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                int rows = borrowPstmt.executeUpdate();
                if (rows < 0) {
                    throw new SQLException("적용된 기록이 없습니다.");
                }
            }

            // 4. 도서 상태 변경 (대출 불가로 해당 도서 처리)
            String updateSql = """
                    UPDATE books SET available = FALSE
                    WHERE id = ?
                    """;

            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();
            }

            // 5. 여기가지 모두 성공했다면 확정
            conn.commit();

        } catch (SQLException e) {
            // 5. 하나라도 실패시 rollback 처리
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException(e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

}

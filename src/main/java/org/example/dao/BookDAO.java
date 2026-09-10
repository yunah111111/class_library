package org.example.dao;

import org.example.dto.Book;
import org.example.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 도서 전체 검색 기능
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        // 1. SQL 구문 준비
        // 2. Connection 객체 생성
        // 3. PreparedStatement 생성
        // 4. 쿼리 실행 후 결과 집합 --> 자료구조 추가해서 리턴

        String sql = """
                SELECT * FROM books ORDER BY id
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    bookList.add(createBook(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bookList;
    }

    // 제목으로 도서 검색 기능
    public List<Book> searchBooksByTitle(String title) {
        List<Book> bookList = new ArrayList<>();

        String sql = """
                SELECT * FROM books WHERE title LIKE ?
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, "%" + title + "%");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    bookList.add(createBook(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return bookList;
    }

    // 도서 등록 기능
    public int addBook(Book book) {
        int rows = 0;

        // available은 default로 설정해서 빼도됨
        String sql = """
                INSERT INTO books (title, author, publisher, publication_year, isbn)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, book.getTitle());
                pstmt.setString(2, book.getAuthor());
                pstmt.setString(3, book.getPublisher());
                pstmt.setInt(4, book.getPublicationYear());
                pstmt.setString(5, book.getIsbn());


                rows = pstmt.executeUpdate();
                System.out.println(rows + "행이 추가되었습니다.");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return 0;
    }

    private Book createBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setIsbn(rs.getString("isbn"));
        book.setAvailable(rs.getBoolean("available"));

        return book;
    }


}

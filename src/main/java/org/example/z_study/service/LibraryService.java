package org.example.z_study.service;

// service는 프로그램의 실제 업무 규칙을 처리하는 곳임

import org.example.dao.BookDAO;
import org.example.dao.BorrowDAO;
import org.example.dao.StudentDAO;
import org.example.z_study.dto.Book;

import java.sql.SQLException;

// [호출 흐름]
// View (사용자의 입력) -> Service (규칙 검사) -> DAO (SQL 실행) -> DB (요청과 응답)
public class LibraryService {

    // 원래 인터페이스 먼저 설계 해야하는데 경험부족으로 생략
    // Service 하나가 DAO 세 개를 소유할 수 있다.
    private BookDAO bookDAO = new BookDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    // 1. 도서 추가
    // 1. 제목과 저자가 비어있는지 확인 (둘 중 하나라도 없으면 중단)
    // 2. 통과하면 DAO에 INSERT 처리를 위임한다.
    public void addBook(Book book) throws SQLException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
        book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 제목과 저자는 필수 입력 항목입니다.");
        }
        // 위임처리
        bookDAO.addBook(book);
    }

}

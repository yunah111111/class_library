package org.example;

import org.example.dao.BookDAO;
import org.example.dao.BorrowDAO;
import org.example.dao.StudentDAO;
import org.example.dto.Book;
import org.example.dto.Borrow;
import org.example.dto.Student;

import java.sql.SQLException;
import java.util.List;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) throws SQLException {

        // 현재 대출 중인 도서 목록 조회
//        BorrowDAO borrowDAO = new BorrowDAO();
//        List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
//        for (int i = 0; i < borrowList.size(); i++) {
//            System.out.println(borrowList.get(i).toString());
//        }

//        // 도서 대출 기능 (트랜잭션)
//        BorrowDAO borrowDAO = new BorrowDAO();
//        borrowDAO.borrowBook(1, 1);


        // 도서 반납 기능 (트랜잭션)
        BorrowDAO borrowDAO = new BorrowDAO();
        List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
        for (int i = 0; i < borrowList.size(); i++) {
            System.out.println(borrowList.get(i).toString());
        }
        borrowDAO.returnBook(3, 1);
    }
}
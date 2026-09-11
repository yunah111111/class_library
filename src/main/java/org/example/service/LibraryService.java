package org.example.service;

// 비즈니스 로직을 처리하는 클래스

import org.example.dao.AdminDAO;
import org.example.dao.BookDAO;
import org.example.dao.BorrowDAO;
import org.example.dao.StudentDAO;
import org.example.dto.Admin;
import org.example.dto.Book;
import org.example.dto.Borrow;
import org.example.dto.Student;

import java.sql.SQLException;
import java.util.List;

// [호출흐름]
// View (사용자의 입력) -> Service (규칙 검사) -> DAO (SQL 실행) -> DB (요청과 응답)
public class LibraryService {

    // (인터페이스 먼저 설계) - 이 단원에서는 생략 X (경험 부족)
    // service 하나가 DAO 세 개를 소유할 수 있다.
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    // 도서 추가
    // 1. 제목과 저자가 비어 있는지 확인 (둘 중 하나라도 없으면 중단)
    // 2. 통과하면 DAO에 INSERT 처리를 위임한다.
    public void addBook(Book book) throws SQLException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
        book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 제목과 저자는 필수 입력 항목입니다.");
        }
        // 위임 처리
        bookDAO.addBook(book);
    }

    // 2. 전체 도서 조회
    // 검사할 규칙이 없으므로 DAO 결과를 그대로 넘긴다.
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    // 3. 도서 제목 검색
    // 3 - 1. 검색어가 비어있는지 확인
    // 3- 2. 통과하면 LIKE 검색을 DAO에 위임한다.
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("검색어를 입력해주세요");
        }
        return bookDAO.searchBooksByTitle(title);
    }

    // 4. 학생 등록
    // 4 - 1. 이름과 학번이 비어 있는지 검사
    // 4 - 2. 통과하면 DAO에 INSERT 처리를 위임함
    // * 유니크 걸려있는 student_id는 DB에서 확인해야함 --> 여기서는 먼저 중복 검사를 안 할 예정
    public void addStudent(Student student) throws SQLException {
        if (student.getName() == null || student.getName().trim().isEmpty() ||
        student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new SQLException("이름과 학번은 필수 입력 항목입니다.");
        }
        studentDAO.addStudent(student);
    }

    // 전체 학생 조회
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudent();
    }

    // 로그인 (학번으로 학생 찾기)
    // 1. 학번이 비어있는지 검사
    // 2. DAO에서 해당 학번을 찾는다.
    // 3. 찾으면 Student를, 없으면 null을 그대로 View에 돌려준다.
    // 여기 코드에서는 비밀번호 없이 학번만 맞으면 로그인되는 것으로 단순화 처리
    public Student getStudentByStudentId(String studentId) throws SQLException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new SQLException("학번을 입력해주세요");
        }
        return studentDAO.getStudentByStudentId(studentId);
    }

    // 도서 대출
    // 1. 도서 ID와 학생 ID가 1 이상인지 검사 (AUTO_INCREMENT는 1부터 시작)
    // 2. 통과하면 DAO의 트랜잭션 메서드에 위임한다.
    // 3. 사실 뷰 단에서 먼저 로그인 여부를 확인하고 수행할 수 있도록 처리가 된다.
    public void borrowBook(int bookId, int studentId) throws SQLException {
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서 ID와 유효한 학생 ID를 입력해주세요");
        }
        borrowDAO.borrowBook(bookId, studentId);
    }

    // 대출 중인 도서 조회
    public List<Borrow> getBorrowedBooks() {
        return borrowDAO.getBorrowedBooks();
    }

    // 도서 반납 기능
    public void returnBook(int bookId, int studentId) throws SQLException {
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서 ID와 유효한 학생 ID를 입력해주세요");
        }
        borrowDAO.returnBook(bookId, studentId);
    }

    // 관리자 로그인(ID와 비밀번호를 확인하는 목적)
    // [처리순서]
    // 1. ID와 비밀번호가 비어 있는지 검사
    // 2. DAO에게 해당 ID의 관리자 정보를 찾는다. (없으면 null)
    // 3. 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호를 비교한다.
    // 4. 일치하면 비밀번호를 지운 Admin 객체를 반환, 아니면 null을 View 클래스에 돌려준다.
    public Admin authenticateAdmin(String adminId, String password) throws SQLException {

        // 1
        if (adminId == null || adminId.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new SQLException("관리자 ID와 비밀번호를 입력해주세요");
        }
        // 2
        Admin admin = adminDAO.findByAdminId(adminId);

        // 3
        if (!password.equals(admin.getPassword())) {
            return null;
        }
        // 4 인증이 끝난 객체에 비밀번호를 남겨 둘 이유가 없으므로 지우고 돌려줍니다.
        admin.setPassword(null);
        return admin;
    }

}

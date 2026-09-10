package org.example.view;

import org.example.dao.BookDAO;
import org.example.dao.StudentDAO;
import org.example.dto.Book;
import org.example.dto.Student;
import org.example.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자의 입출력을 처리하는 View 클래스

// [역할]
// 키보드 입력을 받아 Service에 넘기고, 결과를 화면에 출력한다.
// SQL을 직접 실행하지 않고, 업무 규칙도 판단하지 않음
// "빈 값인가", "숫자인가" 같은 입력 형식을 검사하고 서비스단에 맞는 객체나 값을 구해서 일을 위임한다.
public class LibraryView {
    private final LibraryService libraryService = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 현재 로그인한 학생 정보가 null이 아니라면 로그인된 상태로 보면 된다
    // 만약 null이라면 로그인이 필요한 기능에서 로그인 요청을 먼저 유도해야한다.
    private Integer currentStudentId = null;
    private String currentStudentName = null;
    private Student currentStudent = null;

    // 키보드 입력 값을 받기
    // student Id 확인
    // 로그인 메서드 가져오기
    // student 객체 반환
    // currentStudent에 저장

    public LibraryView() throws SQLException {
        System.out.println("학번을 입력하세요");
        String studentId = scanner.nextLine();

        // 입력한 학번으로 학생 조회
        currentStudent = libraryService.getStudentByStudentId(studentId);

        // 로그인 성공
        currentStudentId = currentStudent.getId();
        currentStudentName = currentStudent.getName();
    }

    // 프로그램 메인 루프
    // [처리순서]
    // 1. 메뉴를 출력한다.
    // 2. 번호를 입력 받는다.
    // 3. 번호에 맞는 메서드를 호출한다.
    // 4. 호출 중 SQLException이 나면 에러 메시지를 출력하고 다시 1번으로 돌아간다.
    // 5. 0번을 입력하면 프로그램 종료 또는 return으로 루프를 빠져나간다.
    public void start() throws SQLException {
        while (true) {
            // 메뉴 출력
            System.out.println("=== 도서관리 시스템 시작");
            System.out.println("=== 도서 관리 시스템 ===");
            System.out.println("[ 로그아웃 상태 ]");
            System.out.println("------------------------");
            System.out.println("<< 메뉴 >>");
            System.out.println("1: 도서 추가 | 2: 도서 목록 | 3: 도서 검색 | 4. 학생 등록 | 5. 학생 조회 | 6. 로그인 | 7. 도서 대출 | 8. 대출 중인 도서 | 9. 도서 반납");

            // 사용자 입력값 받기
            System.out.print("메뉴를 선택해주세요 (1 ~ 9): ");
            int number = scanner.nextInt();
            // 예외처리가 반드시 필요하다.
            switch (number) {
                case 1:
                    libraryService.addBook(Book.builder().build());
                    break;
                case 2:
                    libraryService.getAllBooks();
                    break;
                case 3:
                    libraryService.searchBooksByTitle();
                    break;
                case 4:
                    libraryService.addStudent(currentStudent);
                    break;
                case 5:
                    libraryService.getAllStudents();
                    break;
                case 6:
                    libraryService.getStudentByStudentId(currentStudentName);
                    break;
                case 7:
                    libraryService.borrowBook(libraryServicebo);
                    break;
                case 8:
                    libraryService.getBorrowedBooks();
                    break;
                case 9:
                    libraryService.returnBook();
                    break;
            }
        }
    }

    // 해당 기능을 각각의 메서드로 설계

}

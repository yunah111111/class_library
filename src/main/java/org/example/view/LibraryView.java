package org.example.view;

import org.example.dto.Book;
import org.example.dto.Borrow;
import org.example.dto.Student;
import org.example.service.LibraryService;
import org.example.util.DatabaseUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자의 입출력을 처리하는 View 클래스

// [역할]
// 키보드 입력을 받아 Service 에 넘기고, 결과를 화면에 출력한다.
// SQL 을 직접 실행하지 않고, 업무 규칙토 판단하지 않습니다.
//  "빈 값인가", "숫자인가" 같은 입력 형식을 검사하고 서비스단에 맞는 객체내 값을 구해서 일을 위임한다.
public class LibraryView {

    private final LibraryService service = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 현재 로그인한 학생 정보가 null 아니라면 로그인된 상태로 보면 된다.
    // 만약 null 이라면 로그인이 필요한 기능에서 로그인 요청을 먼저 유도 해야 한다.
    private Integer currentStudentId = null;
    private String currentStudentName = null;
    private Student currentStudent = null;

    // 프로그램 메인 루프
    // [처리순서]
    // 1. 메뉴를 출력한다.
    // 2. 번호를 입력 받는다
    // 3. 번호에 맞는 메서드를 호출한다
    // 4. 호출 중 SQLException 이 나면 에러 메세지를 출력하고 다시 1번으로 돌아간다.
    // 5. 0번을 입력하면 프로그램 종료 또는 return 루프를 빠져 나간다.
    // 프로그램 메인 루프
    public void start() {
        System.out.println("=== 도서관리 시스템 시작 ===");

        while (true) {
            printMenu();
            int choice = readInt("선택: ");

            try {
                switch (choice) {
                    case 1:  addBook();            break;
                    case 2:  listBooks();           break;
                    case 3:  searchBooks();         break;
                    case 4:  addStudent();          break;
                    case 5:  listStudents();        break;
                    case 6:  borrowBook();          break;
                    case 7:  listBorrowedBooks();   break;
                    case 8:  returnBook();          break;
                    case 9:  login();               break;
                    case 10: logout();              break;
                    case 11:
                        System.out.println("프로그램을 종료합니다.");
                        DatabaseUtil.close(); // 커넥션 풀 종료
                        scanner.close();
                        return;
                    default:
                        System.out.println("1~11 사이의 숫자를 입력하세요.");
                }
            } catch (SQLException e) {
                // DB 오류는 사용자에게 친절하게 표시
                System.out.println("오류: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== 도서관리 시스템 ===");
        if (currentStudentId == null) {
            System.out.println("[ 로그아웃 상태 ]");
        } else {
            System.out.println("[ 로그인: " + currentStudentName + " ]");
        }
        System.out.println("──────────────────────");
        System.out.println("1.  도서 추가");
        System.out.println("2.  도서 목록");
        System.out.println("3.  도서 검색");
        System.out.println("4.  학생 등록");
        System.out.println("5.  학생 목록");
        System.out.println("6.  도서 대출");
        System.out.println("7.  대출 중인 도서");
        System.out.println("8.  도서 반납");
        System.out.println("9.  로그인");
        System.out.println("10. 로그아웃");
        System.out.println("11. 종료");
    }

    private void addBook() throws SQLException {
        System.out.print("제목    : ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { System.out.println("제목은 필수입니다."); return; }

        System.out.print("저자    : ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) { System.out.println("저자는 필수입니다."); return; }

        System.out.print("출판사  : ");
        String publisher = scanner.nextLine().trim();

        int year = readInt("출판년도: ");
        if (year < 1 || year > java.time.LocalDate.now().getYear()) {
            System.out.println("유효한 출판년도를 입력하세요.");
            return;
        }

        System.out.print("ISBN    : ");
        String isbn = scanner.nextLine().trim();

        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher.isEmpty() ? null : publisher)
                .publicationYear(year)
                .isbn(isbn.isEmpty() ? null : isbn)
                .available(true)
                .build();
        service.addBook(book);
        System.out.println("'" + title + "' 도서가 추가되었습니다.");
    }

    private void listBooks() throws SQLException {
        List<Book> books = service.getAllBooks();
        System.out.println("\n=== 도서 목록 ===");
        if (books.isEmpty()) {
            System.out.println("등록된 도서가 없습니다.");
        } else {
            System.out.println("─────────────────────────────────────────────────────");
            for (Book b : books) {
                System.out.printf("ID: %2d | %-30s | %-15s | %s%n",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }
    }

    private void searchBooks() throws SQLException {
        System.out.print("검색 제목: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { System.out.println("검색어를 입력해주세요."); return; }

        List<Book> books = service.searchBooksByTitle(title);
        System.out.println("\n=== 검색 결과 ===");
        if (books.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            for (Book b : books) {
                System.out.printf("ID: %2d | %-30s | %-15s | %s%n",
                        b.getId(), b.getTitle(), b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }
    }

    private void addStudent() throws SQLException {
        System.out.print("이름: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("이름은 필수입니다."); return; }

        System.out.print("학번: ");
        String studentId = scanner.nextLine().trim();
        if (studentId.isEmpty()) { System.out.println("학번은 필수입니다."); return; }

        service.addStudent(Student.builder().name(name).studentId(studentId).build());
        System.out.println(name + " 학생이 등록되었습니다.");
    }

    private void listStudents() throws SQLException {
        List<Student> students = service.getAllStudents();
        System.out.println("\n=== 학생 목록 ===");
        if (students.isEmpty()) {
            System.out.println("등록된 학생이 없습니다.");
        } else {
            for (Student s : students) {
                System.out.printf("ID: %2d | %-10s | 학번: %s%n",
                        s.getId(), s.getName(), s.getStudentId());
            }
        }
    }

    private void borrowBook() throws SQLException {
        if (currentStudentId == null) {
            System.out.println("먼저 로그인해주세요. (메뉴 9번)");
            return;
        }
        int bookId = readInt("대출할 도서 ID: ");
        if (bookId <= 0) { System.out.println("유효한 도서 ID 를 입력하세요."); return; }

        service.borrowBook(bookId, currentStudentId);
        System.out.println("대출이 완료되었습니다.");
    }

    private void listBorrowedBooks() throws SQLException {
        List<Borrow> borrows = service.getBorrowedBooks();
        System.out.println("\n=== 대출 중인 도서 ===");
        if (borrows.isEmpty()) {
            System.out.println("현재 대출 중인 도서가 없습니다.");
        } else {
            for (Borrow borrow : borrows) {
                System.out.printf("대출ID: %2d | 도서ID: %2d | 학생ID: %2d | 대출일: %s%n",
                        borrow.getId(), borrow.getBookId(),
                        borrow.getStudentId(), borrow.getBorrowDate());
            }
        }
    }

    private void returnBook() throws SQLException {
        if (currentStudentId == null) {
            System.out.println("먼저 로그인해주세요. (메뉴 9번)");
            return;
        }
        int bookId = readInt("반납할 도서 ID: ");
        if (bookId <= 0) { System.out.println("유효한 도서 ID 를 입력하세요."); return; }

        service.returnBook(bookId, currentStudentId);
        System.out.println("반납이 완료되었습니다.");
    }

    private void login() throws SQLException {
        if (currentStudentId != null) {
            System.out.println("이미 로그인 중입니다. (" + currentStudentName + ")");
            return;
        }
        System.out.print("학번: ");
        String studentId = scanner.nextLine().trim();
        if (studentId.isEmpty()) { System.out.println("학번을 입력해주세요."); return; }

        Student student = service.getStudentByStudentId(studentId);
        if (student == null) {
            System.out.println("존재하지 않는 학번입니다.");
        } else {
            currentStudentId   = student.getId();
            currentStudentName = student.getName();
            System.out.println(currentStudentName + " 님, 환영합니다!");
        }
    }

    private void logout() {
        if (currentStudentId == null) {
            System.out.println("현재 로그인 상태가 아닙니다.");
        } else {
            System.out.println(currentStudentName + " 님이 로그아웃되었습니다.");
            currentStudentId   = null;
            currentStudentName = null;
        }
    }

    // 숫자 입력을 안전하게 처리 (잘못된 입력 시 재요청)
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}

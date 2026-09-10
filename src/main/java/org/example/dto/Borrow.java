package org.example.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder // 좀 더 편하게 객체 생성할 수 있게
// 도서 대출 기록을 담는 DTO
// DTO는 테이블과 꼭 1:1로 맞출 필요 없음
// SQL 실행 결과를 담는 그릇이므로 JOIN으로 가져온 컬럼 결과도 담을 수 있다.
public class Borrow {

    private int id;
    private int bookId;
    private int studentId;
    private String bookTitle;
    private String studentName;
    private LocalDate borrowDate; // LocalDate가 기본
    private Date returnDate; // 옛날 방식

}

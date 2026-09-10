package org.example.z_study.dto;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Borrow {

    private int id;
    private int bookId;
    private int studentId;
    private Date borrowDate;
    private Date returnDate;

}

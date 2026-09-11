package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "password") // exclude는 ToString을 출력할 때 password 부분을 뺌

public class Admin {

    private int id;
    private String adminId;
    private String password;
    private String name;

}

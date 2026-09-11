package org.example.z_study.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString (exclude = "password")
@Builder

public class Admin {

    private int id;
    private String adminId;
    private String password;
    private String name;
}

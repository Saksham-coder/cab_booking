package com.saksham.model;

import com.saksham.domain.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String fullName;
    private String email;

    @Column(unique = true)
    private String mobile;
    private String password;

    private String profilePicture;

    private UserRole role;

}

package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = UserConstants.NAME_SIZE_MAX)
    private String name;

    @Column(nullable = false, length = UserConstants.EMAIL_SIZE_MAX, unique = true)
    private String email;
}

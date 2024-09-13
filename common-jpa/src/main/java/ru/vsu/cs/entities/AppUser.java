package ru.vsu.cs.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.vsu.cs.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState state;
}

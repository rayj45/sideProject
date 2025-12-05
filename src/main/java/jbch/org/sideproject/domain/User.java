package jbch.org.sideproject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "userGroup", length = 100)
    private String userGroup;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Boolean sellerApproved;

    @Builder
    public User(String username, String password, UserRole role, String email, String phone, String userGroup, UserStatus status, Boolean sellerApproved) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.userGroup = userGroup;
        this.status = status;
        this.sellerApproved = sellerApproved;
    }
}

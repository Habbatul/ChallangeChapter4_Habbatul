package com.habbatul.challange4.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String username;

    private String emailAddress;

    private String password;

    //untuk proses bisnis sementara pesan cascade = CascadeType.REMOVE ke orderDetail
    //karena order masih ambigu, apakah order yang completed akan menjadi log history atau tidak
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Order> order;
}

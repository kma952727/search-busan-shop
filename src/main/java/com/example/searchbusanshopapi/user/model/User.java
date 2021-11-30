package com.example.searchbusanshopapi.user.model;

import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "user")
public class User {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="role")
    private String role;
    private String mailToken;
    private boolean isMailCheck = false;

    @JsonManagedReference//순환참조 방어
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) //cascadeType.persist 참조하는객체도 함께 영속성화
    @JoinColumn(name = "user_id")
    private List<Favorite> favorites = new ArrayList<Favorite>();

}

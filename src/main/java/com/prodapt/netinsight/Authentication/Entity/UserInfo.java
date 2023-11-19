package com.prodapt.netinsight.Authentication.Entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "User_Info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String emailid;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

}
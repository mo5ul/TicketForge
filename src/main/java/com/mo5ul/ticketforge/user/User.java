package com.mo5ul.ticketforge.user;
import jakarta.persistence.*;
@Entity 
@Table (name= "users") public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)private Long id;
    @Column(nullable= false, unique=true)private String email;
    @Column(name = "password_hash", nullable = false)private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)private Role role = Role.USER;

    protected User(){}
    public User(String email, String passwordHash){
        this.email=email;
        this.passwordHash=passwordHash;
    }
    public Long getId(){return id; }
    public String getEmail() {return email;}
    public String getPasswordHash(){return passwordHash;}
    public Role getRole() {return role;}
}
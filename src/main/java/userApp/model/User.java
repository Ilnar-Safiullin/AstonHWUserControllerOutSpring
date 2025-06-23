package userApp.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "created_at", nullable = false)
    private LocalDate created_at;


    public User() {
        this.created_at = LocalDate.now();
    }


    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.created_at = LocalDate.now();
    }
}

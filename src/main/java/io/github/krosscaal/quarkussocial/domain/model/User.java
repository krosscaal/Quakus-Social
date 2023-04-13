package io.github.krosscaal.quarkussocial.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
public class User {

/* Obs.: quando usar PanacheEntity não precisa da anotation @Id @GenerateValue o panache ja faz isso*/

/* Obs. 2: se precisar sobreescrever o @Id e o @GenerateValue usar PanacheEntityBase*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

}

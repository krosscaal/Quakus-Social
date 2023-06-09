package io.github.krosscaal.quarkussocial.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "datetime")
    private LocalDateTime dateTime;

    @ManyToOne //muitos posts para um user
    @JoinColumn(name ="user_id")
    private User user;

    @PrePersist //método que realiza algumas operações antes de persist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }
}

package com.example.swaggerprac.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "writer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;
    @Column(nullable = false)
    private String title;
    @Lob
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private int viewCount;

    private PostEntity(String title, User writer, String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.viewCount = 0;
    }

    public static PostEntity create(String title, User writer, String content) {
        return new PostEntity(title, writer, content);
    }

}

package com.example.playgame.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class Game{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "release_date")
    private Date date;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "rating")
    private BigDecimal rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developers_id")
    private Account developer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "games_genres",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "game"
    )
    private List<Purchase> purchases;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bucket_games",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name="bucket_id")
    )
    private List<Bucket> buckets;
}

package com.example.playgame.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "genre_name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_genre_id")
    private Genre parentGenre;

    @OneToMany(mappedBy = "parentGenre", fetch = FetchType.LAZY)
    private List<Genre> subgenres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="games_genres",
            joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Game> games;
}

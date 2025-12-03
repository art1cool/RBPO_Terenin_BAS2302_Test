package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "artists")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ArtistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,  nullable = false)
    private String name;

    @Column(nullable = false)
    private String genre;

    @OneToMany(mappedBy = "artist")
    private List<AlbumEntity> albums;

    @OneToMany(mappedBy = "artist")
    private List<TrackEntity> tracks;
}

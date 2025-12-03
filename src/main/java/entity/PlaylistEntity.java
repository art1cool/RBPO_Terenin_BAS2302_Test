package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlaylistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,  nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
}

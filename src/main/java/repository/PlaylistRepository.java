package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import entity.PlaylistEntity;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, UUID> {
    PlaylistEntity findByName(String name);

//    @Query(value = "SELECT * FROM playlists WHERE user = :user", nativeQuery = true)
//    PlaylistEntity findByUser(String user);
}
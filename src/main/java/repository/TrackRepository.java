package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import entity.TrackEntity;

import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, UUID> {
    TrackEntity findByName(String name);

    @Query(value = "SELECT * FROM tracks WHERE artist = :artist", nativeQuery = true)
    TrackEntity findByArtist(String artist);
}

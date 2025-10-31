package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import entity.ArtistEntity;

import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
        ArtistEntity findByName(String name);

        @Query(value = "SELECT * FROM artists WHERE genre = :genre", nativeQuery = true)
        ArtistEntity findByGenre(String genre);
}

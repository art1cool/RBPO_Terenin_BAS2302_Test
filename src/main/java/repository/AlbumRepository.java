package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import entity.AlbumEntity;

import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, UUID> {
    AlbumEntity findByName(String name);

    @Query(value = "SELECT * FROM albums WHERE year = :year", nativeQuery = true)
    AlbumEntity findByYear(int year);
}

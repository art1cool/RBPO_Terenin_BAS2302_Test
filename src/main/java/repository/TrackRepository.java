package repository;
//1
import entity.TrackEntity;
import entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, UUID> {
    TrackEntity findByName(String name);

    @Query(value = "SELECT * FROM tracks WHERE artist = :artist", nativeQuery = true)
    TrackEntity findByArtist(String artist);

    // Добавляем метод для удаления треков по альбому
    void deleteByAlbum(AlbumEntity album);

    // Добавляем метод для поиска треков по альбому
    List<TrackEntity> findByAlbum(AlbumEntity album);
}

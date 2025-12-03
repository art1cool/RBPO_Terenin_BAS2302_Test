package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import entity.PlaylistEntity;
import entity.UserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, UUID> {
    PlaylistEntity findByName(String name);

    List<PlaylistEntity> findByUser(UserEntity user);

    @Query("SELECT p FROM PlaylistEntity p WHERE p.user.id = :userId AND p.name = :name")
    PlaylistEntity findByNameAndUserId(String name, UUID userId);
}
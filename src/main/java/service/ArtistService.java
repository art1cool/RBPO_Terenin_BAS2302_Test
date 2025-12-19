package service;
//2
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import model.Artist;
import entity.ArtistEntity;
import entity.AlbumEntity;
import entity.TrackEntity;
import repository.ArtistRepository;
import repository.AlbumRepository;
import repository.TrackRepository;
import repository.PlaylistTrackRepository;
import util.MappingUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final MappingUtil mappingUtil;

    public ArtistEntity addArtist(Artist artist) {
        ArtistEntity artistEntity = mappingUtil.toEntity(artist);
        return artistRepository.save(artistEntity);
    }

    @Transactional(readOnly = true)
    public ArtistEntity getArtist(String name) {
        ArtistEntity artist = artistRepository.findByName(name);
        if (artist != null) {
            // Инициализируем ленивые коллекции
            artist.getAlbums().size();
            artist.getTracks().size();
        }
        return artist;
    }

    @Transactional
    public void removeArtist(String name) {
        ArtistEntity artist = artistRepository.findByName(name);
        if (artist != null) {
            // Загружаем альбомы артиста
            List<AlbumEntity> albums = artist.getAlbums();

            // Для каждого альбома удаляем его треки и связи
            for (AlbumEntity album : albums) {
                // Загружаем треки альбома
                List<TrackEntity> albumTracks = trackRepository.findByAlbum(album);
                // Удаляем связи треков в плейлистах, затем удаляем треки
                for (TrackEntity track : albumTracks) {
                    playlistTrackRepository.deleteByTrack(track);
                    trackRepository.delete(track);
                }
                // Удаляем альбом
                albumRepository.delete(album);
            }

            // Удаляем одиночные треки артиста (не в альбомах)
            List<TrackEntity> standaloneTracks = artist.getTracks();
            for (TrackEntity track : standaloneTracks) {
                // Проверяем, не был ли трек уже удален (в составе альбома)
                if (track != null && trackRepository.existsById(track.getId())) {
                    playlistTrackRepository.deleteByTrack(track);
                    trackRepository.delete(track);
                }
            }

            // Удаляем артиста
            artistRepository.delete(artist);
        }
    }

    public ArtistEntity updateArtist(String name, Artist updatedFields) {
        ArtistEntity existing = artistRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        // Обновляем только переданные поля
        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getGenre() != null && !updatedFields.getGenre().isBlank()) {
            existing.setGenre(updatedFields.getGenre());
        }

        return artistRepository.save(existing);
    }
}

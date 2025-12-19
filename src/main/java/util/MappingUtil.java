package util;
//1
import org.springframework.stereotype.Component;
import entity.ArtistEntity;
import entity.AlbumEntity;
import entity.TrackEntity;
import model.Artist;
import model.Album;
import model.Track;
import entity.UserEntity;
import entity.PlaylistEntity;
import model.User;
import model.Playlist;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Component
public class MappingUtil {

    // Artist преобразования
    public Artist toDto(ArtistEntity entity) {
        Artist dto = new Artist();
        dto.setName(entity.getName());
        dto.setGenre(entity.getGenre());

        // Преобразуем альбомы артиста
        if (entity.getAlbums() != null) {
            List<Album> albums = new ArrayList<>();
            for (AlbumEntity albumEntity : entity.getAlbums()) {
                albums.add(toAlbumDto(albumEntity, false)); // false - не включать треки в альбомы
            }
            dto.setAlbums(albums);
        }

        // Преобразуем треки артиста
        if (entity.getTracks() != null) {
            List<Track> tracks = new ArrayList<>();
            for (TrackEntity trackEntity : entity.getTracks()) {
                tracks.add(toTrackDto(trackEntity, false)); // false - не включать полную информацию об артисте и альбоме
            }
            dto.setTracks(tracks);
        }

        return dto;
    }

    public ArtistEntity toEntity(Artist dto) {
        ArtistEntity entity = new ArtistEntity();
        entity.setName(dto.getName());
        entity.setGenre(dto.getGenre());
        return entity;
    }

    // Album преобразования
    // Album преобразования - обновляем публичный метод
    public Album toDto(AlbumEntity entity) {
        return toAlbumDto(entity, true); // true - включать треки
    }

    // Обновляем приватный метод toAlbumDto
    private Album toAlbumDto(AlbumEntity entity, boolean includeTracks) {
        Album dto = new Album();
        dto.setName(entity.getName());
        dto.setYear(entity.getYear());

        if (entity.getArtist() != null) {
            Artist artistDto = new Artist();
            artistDto.setName(entity.getArtist().getName());
            artistDto.setGenre(entity.getArtist().getGenre());
            dto.setArtist(artistDto);
        }

        // Включаем треки если запрошено
        if (includeTracks && entity.getTracks() != null && !entity.getTracks().isEmpty()) {
            List<Track> trackDtos = entity.getTracks().stream()
                    .map(trackEntity -> {
                        // Создаем DTO для трека с минимальной информацией
                        Track trackDto = new Track();
                        trackDto.setName(trackEntity.getName());
                        trackDto.setDuration(trackEntity.getDuration());

                        // Добавляем артиста трека
                        if (trackEntity.getArtist() != null) {
                            Artist trackArtist = new Artist();
                            trackArtist.setName(trackEntity.getArtist().getName());
                            trackArtist.setGenre(trackEntity.getArtist().getGenre());
                            trackDto.setArtist(trackArtist);
                        }

                        // Не включаем альбом в трек, чтобы избежать циклических ссылок
                        return trackDto;
                    })
                    .collect(Collectors.toList());
            dto.setTracks(trackDtos);
        }

        return dto;
    }


    public AlbumEntity toEntity(Album dto) {
        AlbumEntity entity = new AlbumEntity();
        entity.setName(dto.getName());
        entity.setYear(dto.getYear());
        // Artist будет установлен в сервисе после поиска по имени
        return entity;
    }

    // Track преобразования
    public Track toDto(TrackEntity entity) {
        return toTrackDto(entity, true);
    }

    private Track toTrackDto(TrackEntity entity, boolean includeFullInfo) {
        Track dto = new Track();
        dto.setName(entity.getName());
        dto.setDuration(entity.getDuration());

        if (entity.getArtist() != null) {
            Artist artistDto = new Artist();
            artistDto.setName(entity.getArtist().getName());
            artistDto.setGenre(entity.getArtist().getGenre());
            dto.setArtist(artistDto);
        }

        if (includeFullInfo && entity.getAlbum() != null) {
            Album albumDto = new Album();
            albumDto.setName(entity.getAlbum().getName());
            albumDto.setYear(entity.getAlbum().getYear());
            if (entity.getAlbum().getArtist() != null) {
                Artist albumArtistDto = new Artist();
                albumArtistDto.setName(entity.getAlbum().getArtist().getName());
                albumArtistDto.setGenre(entity.getAlbum().getArtist().getGenre());
                albumDto.setArtist(albumArtistDto);
            }
            dto.setAlbum(albumDto);
        }

        return dto;
    }

    public TrackEntity toEntity(Track dto) {
        TrackEntity entity = new TrackEntity();
        entity.setName(dto.getName());
        entity.setDuration(dto.getDuration());
        // Artist и Album будут установлены в сервисе после поиска по имени
        return entity;
    }

    public User toDto(UserEntity entity) {
        User dto = new User();
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        // НЕ устанавливаем пароль - не выводим его вообще

        // Добавляем список имен плейлистов пользователя
        if (entity.getPlaylist() != null) {
            List<String> playlists = entity.getPlaylist().stream()
                    .map(PlaylistEntity::getName)
                    .collect(Collectors.toList());
            dto.setPlaylists(playlists);
        }

        return dto;
    }

    public UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword()); // пароль будет закодирован в сервисе
        // playlistNames не преобразуем в entity
        return entity;
    }

    // Playlist преобразования
    public Playlist toDto(PlaylistEntity entity) {
        Playlist dto = new Playlist();
        dto.setName(entity.getName());

        // Преобразуем пользователя (без пароля)
        if (entity.getUser() != null) {
            User userDto = new User();
            userDto.setName(entity.getUser().getName());
            userDto.setEmail(entity.getUser().getEmail());
            // НЕ устанавливаем пароль
            dto.setUser(userDto);
        }

        // Для списка треков в плейлисте нужно получить их через PlaylistTrackRepository
        // Это будет сделано в контроллере отдельно

        return dto;
    }

    public PlaylistEntity toEntity(Playlist dto) {
        PlaylistEntity entity = new PlaylistEntity();
        entity.setName(dto.getName());
        // User будет установлен в сервисе после поиска по имени
        return entity;
    }
}
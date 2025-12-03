package service;

import entity.AlbumEntity;
import model.Album;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import repository.AlbumRepository;
import entity.ArtistEntity;
import repository.ArtistRepository;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    public AlbumEntity getAlbum(String name) {
        return albumRepository.findByName(name);
    }

    public AlbumEntity addAlbum(Album album) {
        ArtistEntity artist = artistRepository.findByName(album.getArtist().getName());
        if (artist == null) {
            throw new RuntimeException("Artist not found: " + album.getArtist().getName());
        }
        AlbumEntity albumEntity = new  AlbumEntity();
        albumEntity.setName(album.getName());
        albumEntity.setYear(album.getYear());
        albumEntity.setArtist(artist);
        return albumRepository.save(albumEntity);
    }
    public void removeAlbum(String name) {
        albumRepository.delete(getAlbum(name));
    }
    public AlbumEntity updateAlbum(String name, Album updatedFields) {
        AlbumEntity existing = albumRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getYear() != 0) {
            existing.setYear(updatedFields.getYear());
        }
        if (updatedFields.getArtist() != null
                && updatedFields.getArtist().getName() != null
                && !updatedFields.getArtist().getName().isBlank()) {

            ArtistEntity artist = artistRepository.findByName(updatedFields.getArtist().getName());
            if (artist == null) {
                throw new RuntimeException("Artist not found: " + updatedFields.getArtist().getName());
            }
            existing.setArtist(artist);
        }

        return albumRepository.save(existing);
    }
}
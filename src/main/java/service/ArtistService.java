package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import model.Artist;

import entity.ArtistEntity;
import repository.ArtistRepository;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    public ArtistEntity addArtist(Artist artist) {
        ArtistEntity artistEntity = new  ArtistEntity();
        artistEntity.setName(artist.getName());
        artistEntity.setGenre(artist.getGenre());
        return artistRepository.save(artistEntity);
    }
    public ArtistEntity getArtist(String name) {
        return artistRepository.findByName(name);
    }
    public void removeArtist(String name) {
        artistRepository.delete(getArtist(name));
    }

    public ArtistEntity updateArtist(String name, Artist updatedFields) {
        ArtistEntity existing = artistRepository.findByName(name);
        if (existing == null) {
            return null; // не найден
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

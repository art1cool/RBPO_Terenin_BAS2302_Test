package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import model.Playlist;
import entity.PlaylistEntity;
import repository.PlaylistRepository;
import entity.UserEntity;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    public PlaylistEntity getPlaylist(String name) {
        return playlistRepository.findByName(name);
    }
    public PlaylistEntity addPlaylist(Playlist playlist) {
        UserEntity user = userRepository.findByName(playlist.getUser().getName());
        if (user == null) {
            throw new RuntimeException("User not found: " + playlist.getUser().getName());
        }
        PlaylistEntity playlistEntity = new  PlaylistEntity();
        playlistEntity.setName(playlist.getName());
        playlistEntity.setUser(user);
        return playlistRepository.save(playlistEntity);
    }
    public void removePlaylist(String name) {
        playlistRepository.delete(getPlaylist(name));
    }
    public PlaylistEntity updatePlaylist(String name, Playlist updatedFields) {
        PlaylistEntity existing = playlistRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getUser() != null
                && updatedFields.getUser().getName() != null
                && !updatedFields.getUser().getName().isBlank()) {

            UserEntity user = userRepository.findByName(updatedFields.getUser().getName());
            if (user == null) {
                throw new RuntimeException("User not found: " + updatedFields.getUser().getName());
            }
            existing.setUser(user);
        }

        return playlistRepository.save(existing);
    }
}

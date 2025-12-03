package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import model.Playlist;
import entity.PlaylistEntity;
import repository.PlaylistRepository;
import entity.UserEntity;
import repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Current user not found with email: " + email);
        }
        return user.get();
    }

    private boolean isPlaylistOwner(PlaylistEntity playlist) {
        UserEntity currentUser = getCurrentUser();
        return playlist.getUser().getId().equals(currentUser.getId());
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public PlaylistEntity getPlaylist(String name) {
        PlaylistEntity playlist = playlistRepository.findByName(name);
        if (playlist == null) {
            throw new RuntimeException("Playlist not found: " + name);
        }
        return playlist;
    }

    public PlaylistEntity addPlaylist(Playlist playlist) {
        UserEntity user = userRepository.findByName(playlist.getUser().getName());
        if (user == null) {
            throw new RuntimeException("User not found: " + playlist.getUser().getName());
        }

        UserEntity currentUser = getCurrentUser();
        if (!currentUser.getId().equals(user.getId()) && !isAdmin()) {
            throw new AccessDeniedException("You can only create playlists for yourself");
        }

        PlaylistEntity playlistEntity = new  PlaylistEntity();
        playlistEntity.setName(playlist.getName());
        playlistEntity.setUser(user);
        return playlistRepository.save(playlistEntity);
    }

    public void removePlaylist(String name) {
        PlaylistEntity playlist = playlistRepository.findByName(name);
        if (playlist == null) {
            throw new RuntimeException("Playlist not found: " + name);
        }

        if (!isPlaylistOwner(playlist) && !isAdmin()) {
            throw new AccessDeniedException("You can only delete your own playlists");
        }

        playlistRepository.delete(playlist);
    }

    public PlaylistEntity updatePlaylist(String name, Playlist updatedFields) {
        PlaylistEntity existing = playlistRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (!isPlaylistOwner(existing) && !isAdmin()) {
            throw new AccessDeniedException("You can only update your own playlists");
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

            UserEntity currentUser = getCurrentUser();
            if (!isAdmin() && !user.getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only assign playlists to yourself");
            }

            existing.setUser(user);
        }

        return playlistRepository.save(existing);
    }
}

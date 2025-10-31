package controller;


import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import model.Playlist;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.PlaylistService;

import static org.springframework.http.HttpStatus.CREATED;
import entity.PlaylistEntity;
@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
@Validated


public class PlaylistController {
    private final PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<PlaylistEntity> getPlaylist(String name) {
        return ResponseEntity.ok()
                .body(playlistService.getPlaylist(name));
    }

    @PostMapping
    public ResponseEntity<PlaylistEntity> addPlaylist(
            @Valid @RequestBody Playlist playlist){
        return ResponseEntity.status(CREATED)
                .header("Name", playlist.getName())
                .body(playlistService.addPlaylist(playlist));
    }

    @DeleteMapping("by-name/{name}")
    public ResponseEntity<Void> removePlaylist(@PathVariable String name) {
        playlistService.removePlaylist(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{name}")
    public ResponseEntity<PlaylistEntity> updatePlaylist(
            @PathVariable String name,
            @RequestBody Playlist updatedFields) {

        PlaylistEntity updated = playlistService.updatePlaylist(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}

package controller;

import entity.AlbumEntity;
import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import model.Album;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.AlbumService;

import static org.springframework.http.HttpStatus.CREATED;
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@Validated


public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<AlbumEntity> getAlbum(String name) {
        return ResponseEntity.ok()
                .body(albumService.getAlbum(name));
    }

    @PostMapping
    public ResponseEntity<AlbumEntity> addAlbum(
            @Valid @RequestBody Album album){
        return ResponseEntity.status(CREATED)
                .header("Name", album.getName())
                .body(albumService.addAlbum(album));
    }

    @DeleteMapping("by-name/{name}")
    public ResponseEntity<Void> removeAlbum(@PathVariable String name) {
        albumService.removeAlbum(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{name}")
    public ResponseEntity<AlbumEntity> updateAlbum(
            @PathVariable String name,
            @RequestBody Album updatedFields) {

        AlbumEntity updated = albumService.updateAlbum(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}


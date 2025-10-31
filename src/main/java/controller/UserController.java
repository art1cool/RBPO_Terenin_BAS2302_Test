package controller;

import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import static org.springframework.http.HttpStatus.CREATED;
import entity.UserEntity;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated


public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserEntity> getUser(String name) {
        return ResponseEntity.ok()
                .body(userService.getUser(name));
    }

    @PostMapping
    public ResponseEntity<UserEntity> addUser(
            @Valid @RequestBody User user){
        return ResponseEntity.status(CREATED)
                .header("Name", user.getName())
                .body(userService.addUser(user));
    }

    @DeleteMapping("by-name/{name}")
    public ResponseEntity<Void> removeUser(@PathVariable String name) {
        userService.removeUser(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{name}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable String name,
            @RequestBody User updatedFields) {

        UserEntity updated = userService.updateUser(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}

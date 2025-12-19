package model;
//1
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List; // Добавляем импорт

@Data
public class Playlist {
    @NotBlank
    private String name;

    @NotNull
    private User user;

    private List<String> tracks; // Добавляем поле для списка имен треков
}

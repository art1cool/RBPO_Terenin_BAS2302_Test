package model;
//1
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List; // Добавляем импорт

@Data
public class Album {
    @NotBlank
    private String name;
    private int year;
    @NotNull
    private Artist artist;
    private List<Track> tracks; // Добавляем поле для списка треков
}

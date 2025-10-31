package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Album {
    @NotBlank
    private String name;
    private int year;
    @NotNull
    private Artist artist;
}

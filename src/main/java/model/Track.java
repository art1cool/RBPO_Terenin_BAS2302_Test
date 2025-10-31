package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Track {
    @NotBlank
    private String name;
    @NotNull
    private Artist artist;
}

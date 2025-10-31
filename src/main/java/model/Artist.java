package model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Artist
{
    @NotBlank
    private String name;
    @NotBlank
    private String genre;
}

package com.bwalczak.note.domain;

import com.bwalczak.note.enums.Level;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class Note implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title should not be null")
    @NotEmpty(message = "Title should not be empty")
    private String title;

    @NotNull(message = "Description should not be null")
    @NotEmpty(message = "Description should not be empty")
    private String description;

    private Level level;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Europe/Warsaw")
    private LocalDateTime createdAt;
}

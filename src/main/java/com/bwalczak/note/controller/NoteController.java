package com.bwalczak.note.controller;

import com.bwalczak.note.domain.HttpResponse;
import com.bwalczak.note.domain.Note;
import com.bwalczak.note.enums.Level;
import com.bwalczak.note.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<HttpResponse<Note>> getAllNotes() {
        return ResponseEntity.ok().body(noteService.getAllNotes());
    }

    @GetMapping("/filter")
    public ResponseEntity<HttpResponse<Note>> getNotesByLevel(@RequestParam("level") Level level) {
        return ResponseEntity.ok().body(noteService.getNotesByLevel(level));
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse<Note>> updateNote(@RequestBody @Valid Note note) {
        return ResponseEntity.ok().body(noteService.updateNote(note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<HttpResponse<Note>> deleteNote(@PathVariable("noteId") Long id) {
        return ResponseEntity.ok().body(noteService.deleteNote(id));
    }

    @PostMapping("/add")
    public ResponseEntity<HttpResponse<Note>> createNote(@RequestBody @Valid Note note) {
        return ResponseEntity.created(
                URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/notes/add").toUriString()))
                .body(noteService.createNote(note));
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse<?>> handleWhitelabelError(HttpServletRequest request) {
        return ResponseEntity.ok(noteService.handleWhitelabelError(request));
    }
}

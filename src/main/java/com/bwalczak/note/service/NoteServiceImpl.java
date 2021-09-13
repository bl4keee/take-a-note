package com.bwalczak.note.service;

import com.bwalczak.note.domain.HttpResponse;
import com.bwalczak.note.domain.Note;
import com.bwalczak.note.enums.Level;
import com.bwalczak.note.exception.NoteNotFoundException;
import com.bwalczak.note.repo.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.bwalczak.note.Utils.DateUtil.dateTimeFormatter;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService{

    private final NoteRepository noteRepository;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final static String NOTE_NOT_FOUND = "The note was not found on the server!";

    public HttpResponse<Note> getAllNotes() {
        log.info("Fetching all the notes...");
        return HttpResponse.<Note>builder()
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .data(filterByCreatedAt(noteRepository.findAll()))
                .message(noteRepository.count() > 0 ? noteRepository.count() + " notes retrieved" : "No notes to display!")
                .build();
                // later on, update to return a page of notes, not all of the notes from db - do not use findAll
    }

    public HttpResponse<Note> getNotesByLevel(Level level) {
        log.info("Fetching all the notes by level {}", level);

        List<Note> notes = noteRepository.getByLevel(level);
        return HttpResponse.<Note>builder()
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .data(filterByCreatedAt(notes))
                .message(notes.size() + " of the " + level + " level")
                .build();
    }

    public HttpResponse<Note> createNote(Note note) {
        log.info("Saving new note to the database...");

        note.setCreatedAt(timestamp);

        return HttpResponse.<Note>builder()
                .status(CREATED)
                .statusCode(CREATED.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .data(singletonList(noteRepository.save(note)))
                .message("Note has been created successfully!")
                .build();
    }

    public HttpResponse<Note> updateNote(Note note) throws NoteNotFoundException {
        log.info("Updating the note...");

        Optional<Note> optionalNote = ofNullable(noteRepository.findById(note.getId()))
                .orElseThrow(() -> new NoteNotFoundException(NOTE_NOT_FOUND));

        Note updatedNote = getUpdatedNote(note);

        return HttpResponse.<Note>builder()
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .data(singletonList(updatedNote))
                .message("Note has been updated successfully!")
                .build();
    }

    public HttpResponse<Note> deleteNote(Long id) throws NoteNotFoundException {
        log.info("Deleting the note with id {}", id);

        Optional<Note> optionalNote = ofNullable(noteRepository.findById(id))
                .orElseThrow(() -> new NoteNotFoundException(NOTE_NOT_FOUND));
        optionalNote.ifPresent(noteRepository::delete);

        return HttpResponse.<Note>builder()
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .data(singletonList(optionalNote.get()))
                .message("Note has been deleted!")
                .build();
    }

    public HttpResponse<?> handleWhitelabelError(HttpServletRequest request) {
        return HttpResponse.builder()
                .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server!")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .timeStamp(timestamp.format(dateTimeFormatter()))
                .build();
    }

    private Note getUpdatedNote(Note note) {
        Optional<Note> optionalNote = ofNullable(noteRepository.findById(note.getId()))
                .orElseThrow(() -> new NoteNotFoundException(NOTE_NOT_FOUND));

        Note noteToUpdate = optionalNote.get();
        noteToUpdate.setId(note.getId());
        noteToUpdate.setTitle(note.getTitle());
        noteToUpdate.setDescription(note.getDescription());
        noteToUpdate.setLevel(note.getLevel());
        noteRepository.save(noteToUpdate);

        return noteToUpdate;
    }

    private List<Note> filterByCreatedAt(List<Note> notes) {
        return notes.stream()
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}

package com.bwalczak.note.service;

import com.bwalczak.note.domain.HttpResponse;
import com.bwalczak.note.domain.Note;
import com.bwalczak.note.enums.Level;

import javax.servlet.http.HttpServletRequest;

public interface NoteService {

    HttpResponse<Note> getAllNotes();

    HttpResponse<Note> deleteNote(Long id);

    HttpResponse<Note> createNote(Note note);

    HttpResponse<Note> updateNote(Note note);

    HttpResponse<Note> getNotesByLevel(Level level);

    HttpResponse<?> handleWhitelabelError(HttpServletRequest request);
}

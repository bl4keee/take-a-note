package com.bwalczak.note.repo;

import com.bwalczak.note.domain.Note;
import com.bwalczak.note.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> getByLevel(Level level);

    void deleteNoteById(Long id);
}

package com.notes.backend.controller;

import com.notes.backend.entity.Note;
import com.notes.backend.entity.Reminder;
import com.notes.backend.entity.User;
import com.notes.backend.repository.NoteRepository;
import com.notes.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import com.notes.backend.dto.NoteDto;
import com.notes.backend.entity.Note;
import com.notes.backend.entity.User;
import com.notes.backend.exception.ResourceNotFoundException;
import com.notes.backend.repository.NoteRepository;
import com.notes.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:8080")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

/*
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNotes() {
        List<Map<String, Object>> result = noteRepository.findAll().stream()
                .map(this::convertNoteToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
*/

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNotes(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        List<Map<String, Object>> result = noteRepository.findAll(sort).stream()
                .map(this::convertNoteToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNoteById(@PathVariable Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заметка не найдена"));
        return ResponseEntity.ok(convertNoteToMap(note));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNote(@Valid @RequestBody NoteDto newNote) {
        User user = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Note note = new Note(
                newNote.getTitle(),
                newNote.getContent(),
                newNote.getTags(),
                user
        );

        Note savedNote = noteRepository.save(note);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "created");
        response.put("note", convertNoteToMap(savedNote));
        response.put("message", "Заметка создана успешно");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteDto updatedNote) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заметка не найдена"));

        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());
        note.setTags(updatedNote.getTags());
        Note savedNote = noteRepository.save(note);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "updated");
        response.put("note", convertNoteToMap(savedNote));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заметка не найдена");
        }
        noteRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted", "message", "Заметка " + id + " удалена"));
    }

    private Map<String, Object> convertNoteToMap(Note note) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", note.getId());
        map.put("title", note.getTitle());
        map.put("content", note.getContent());
        map.put("tags", note.getTags());
        map.put("date", note.getCreatedAt().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        if (!note.getReminders().isEmpty()) {
            Reminder reminder = note.getReminders().iterator().next();
            Map<String, Object> reminderMap = new HashMap<>();
            reminderMap.put("id", reminder.getId());
            reminderMap.put("email", reminder.getEmail());
            reminderMap.put("reminderTime", reminder.getReminderTime().toString());
            reminderMap.put("isSent", reminder.getIsSent());
            map.put("reminder", reminderMap);
        } else {
            map.put("reminder", null);
        }

        return map;
    }
}
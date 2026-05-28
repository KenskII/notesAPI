package com.notes.backend.controller;

import com.notes.backend.entity.Note;
import com.notes.backend.entity.Reminder;
import com.notes.backend.repository.NoteRepository;
import com.notes.backend.repository.ReminderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
import com.notes.backend.dto.ReminderDto;
import com.notes.backend.entity.Note;
import com.notes.backend.entity.Reminder;
import com.notes.backend.exception.ResourceNotFoundException;
import com.notes.backend.repository.NoteRepository;
import com.notes.backend.repository.ReminderRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "http://localhost:8080")
public class ReminderController {

    private final ReminderRepository reminderRepository;
    private final NoteRepository noteRepository;

    public ReminderController(ReminderRepository reminderRepository, NoteRepository noteRepository) {
        this.reminderRepository = reminderRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllReminders() {
        List<Map<String, Object>> result = reminderRepository.findAll().stream()
                .map(this::convertReminderToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getReminderById(@PathVariable Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Напоминание не найдено"));
        return ResponseEntity.ok(convertReminderToMap(reminder));
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Map<String, Object>>> getRemindersByNoteId(@PathVariable Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Заметка не найдена"));

        List<Map<String, Object>> result = reminderRepository.findByNote(note).stream()
                .map(this::convertReminderToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReminder(@Valid @RequestBody ReminderDto newReminder) {
        Note note = noteRepository.findById(newReminder.getNoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Заметка не найдена"));

        LocalDateTime reminderTime;
        try {
            reminderTime = LocalDateTime.parse(newReminder.getReminderTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат даты и времени. Используйте ISO формат: yyyy-MM-dd'T'HH:mm:ss");
        }

        Reminder reminder = new Reminder(
                newReminder.getEmail(),
                reminderTime,
                note
        );

        Reminder savedReminder = reminderRepository.save(reminder);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "created");
        response.put("reminder", convertReminderToMap(savedReminder));
        response.put("message", "Напоминание создано (отправка имитируется)");

        System.out.println("✉️ Создано напоминание: " + savedReminder.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/send")
    public ResponseEntity<Map<String, String>> markAsSent(@PathVariable Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Напоминание не найдено"));

        reminder.setIsSent(true);
        reminderRepository.save(reminder);
        System.out.println("✅ Напоминание " + id + " отмечено как отправленное");

        return ResponseEntity.ok(Map.of("status", "sent", "message", "Напоминание отмечено как отправленное"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReminder(@PathVariable Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Напоминание не найдено");
        }
        reminderRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted", "message", "Напоминание удалено"));
    }

    private Map<String, Object> convertReminderToMap(Reminder reminder) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", reminder.getId());
        map.put("email", reminder.getEmail());
        map.put("reminderTime", reminder.getReminderTime().toString());
        map.put("isSent", reminder.getIsSent());
        map.put("noteId", reminder.getNote().getId());
        return map;
    }
}
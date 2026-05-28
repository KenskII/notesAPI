package com.notes.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class ReminderDto {
    @NotNull(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotNull(message = "reminderTime обязателен")
    private String reminderTime;

    @NotNull(message = "noteId обязателен")
    private Long noteId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
}
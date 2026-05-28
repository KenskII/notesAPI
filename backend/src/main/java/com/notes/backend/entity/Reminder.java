package com.notes.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    public Reminder() {}

    public Reminder(String email, LocalDateTime reminderTime, Note note) {
        this.email = email;
        this.reminderTime = reminderTime;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalDateTime reminderTime) { this.reminderTime = reminderTime; }
    public Boolean getIsSent() { return isSent; }
    public void setIsSent(Boolean sent) { isSent = sent; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
}
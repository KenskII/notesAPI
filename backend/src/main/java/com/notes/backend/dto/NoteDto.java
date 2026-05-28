package com.notes.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class NoteDto {
    @NotBlank(message = "Не указан обязательный параметр title")
    private String title;

    private String content;
    private String tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
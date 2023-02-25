package com.devsss.aoeba.web.dto;

import java.util.ArrayList;
import java.util.Date;

import com.devsss.aoeba.service.domain.Note;
import lombok.Data;

@Data
public class NoteEx {

    String id;

    Date createdAt;

    Date updatedAt;

    Date deletedAt;

    String title;

    String content;

    Integer views;

    String img;

    String category;

    ArrayList<Tag> tags;

    Integer mark;

    public NoteEx() {

    }

    public NoteEx(Note note) {
        this.setId(note.getId());
        this.setContent(note.getContent());
        this.setCreatedAt(note.getCreatedAt());
        this.setDeletedAt(note.getDeletedAt());
        this.setTitle(note.getTitle());
        this.setUpdatedAt(note.getUpdatedAt());
        this.setViews(note.getViews());
        this.setTags(new ArrayList<Tag>());
        this.setImg(note.getImg());
        this.setCategory(note.getCategory());
        this.setMark(note.getMark());
        if (note.getTags() != null && !note.getTags().equals("")) {
            String[] tag = note.getTags().split(",");
            for (String name : tag) {
                Tag t = new Tag();
                t.setName(name);
                this.getTags().add(t);
            }
        }
    }

    public Note toNote() {
        final Note note = new Note();
        note.setId(this.getId());
        note.setContent(this.getContent());
        note.setCreatedAt(this.getCreatedAt());
        note.setDeletedAt(this.getDeletedAt());
        note.setTitle(this.getTitle());
        note.setUpdatedAt(this.getUpdatedAt());
        note.setViews(this.getViews());
        note.setImg(this.getImg());
        note.setCategory(this.getCategory());
        note.setMark(this.getMark());
        StringBuffer tags = new StringBuffer();
        if (this.getTags() != null) {
            this.getTags().forEach(t -> {
                tags.append(t.getName()).append(",");
            });

            if (tags.length() > 0 && tags.charAt(tags.length() - 1) == ',') {
                tags.deleteCharAt(tags.length() - 1);
            }

            note.setTags(tags.toString());
        }

        return note;
    }
}

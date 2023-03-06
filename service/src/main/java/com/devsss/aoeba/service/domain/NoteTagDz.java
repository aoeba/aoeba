package com.devsss.aoeba.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
public class NoteTagDz {

    @Id
    private String id;

    private String noteId;

    private String tagName;

    private Date createdAt;

    private Date deletedAt;

    private Date updatedAt;

    public NoteTagDz(String noteId, String tagName) {
        this.noteId = noteId;
        this.tagName = tagName;
        this.id = noteId + tagName;
    }

}

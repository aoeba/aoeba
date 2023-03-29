package com.devsss.aoeba.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class NoteTagDz {

    @Id
    private String id;

    private String noteId;

    private String tagName;

    public NoteTagDz(String noteId, String tagName) {
        this.noteId = noteId;
        this.tagName = tagName;
        this.id = noteId + tagName;
    }

}

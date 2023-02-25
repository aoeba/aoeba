package com.devsss.aoeba.service.domain;

import lombok.Data;

import java.util.Date;

@Data
public class NoteTagDz {

    private String noteId;

    private String tagName;

    private Date createdAt;

    private Date deletedAt;

    private Date updatedAt;

}

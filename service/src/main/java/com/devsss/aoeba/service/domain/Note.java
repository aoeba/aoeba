package com.devsss.aoeba.service.domain;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Data
@ToString
public class Note {

    @Id
    private String id;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private String tags;

    private String title;

    private LocalDateTime updatedAt;

    private Integer views;

    private String category;

    private String img;

    private Integer mark;

}
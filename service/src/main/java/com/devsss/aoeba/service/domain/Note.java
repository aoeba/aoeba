package com.devsss.aoeba.service.domain;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Note {

    private String id;

    private String content;

    private Date createdAt;

    private Date deletedAt;

    private String tags;

    private String title;

    private Date updatedAt;

    private Integer views;

    private String category;

    private String img;

    private Integer mark;

}
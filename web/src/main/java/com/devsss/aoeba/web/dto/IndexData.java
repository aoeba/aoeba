package com.devsss.aoeba.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class IndexData {

    List<NoteEx> noteList;
    List<Tag> tags;
    List<Category> categories;
    List<NoteEx> swiperNote;
}

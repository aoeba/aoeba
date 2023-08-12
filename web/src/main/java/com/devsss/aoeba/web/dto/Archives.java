package com.devsss.aoeba.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Archives {

    private int month;
    private int day;

    private NoteEx note;

}

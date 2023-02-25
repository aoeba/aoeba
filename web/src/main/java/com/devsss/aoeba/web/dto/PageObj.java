package com.devsss.aoeba.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageObj<E> {

    int number;
    int totalPages;
    long total;
    int size;
    List<E> content;
    boolean hasNext;
    boolean hasContent;
}
package com.devsss.aoeba.service.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {
    Long total;
    int pageNo;
    int pageSize;
    int pages;
    boolean hasNext;
    boolean hasContent;
    List<T> content;
}

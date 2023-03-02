package com.devsss.aoeba.web.dto;

import lombok.Data;

@Data
public class DirItem {

    String type;
    int length;
    String name;
    Boolean isFolder;
    String url;
    int last_modified;
}

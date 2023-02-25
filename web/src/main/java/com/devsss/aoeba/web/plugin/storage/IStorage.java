package com.devsss.aoeba.web.plugin.storage;

import org.springframework.web.multipart.MultipartFile;

public interface IStorage {

    Response mkDir(String path) throws Exception;

    Response rmDir(String path) throws Exception;

    /**
     * result:
     * [{"type": "image/jpeg","length": 4237,"name": "foo.jpg","last_modified":
     * 1415096225,"isFolder" : true, "url":"https://www.mk95.cn/*****"}]
     *
     * @param path
     * @return
     * @throws Exception
     */
    Response readDir(String path) throws Exception;

    Response writeFile(String filePath, MultipartFile file) throws Exception;

    Response readFile(String filePath) throws Exception;

    Response deleteFile(String filePath) throws Exception;

    Response moveFile(String from, String to) throws Exception;
}

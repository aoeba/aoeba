package com.devsss.aoeba.web.plugin.storage.impl;

import com.devsss.aoeba.web.plugin.storage.IStorage;
import com.devsss.aoeba.web.plugin.storage.Response;
import com.upyun.RestManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpyunStorage implements IStorage {

    private RestManager restManager;

    @Value("${aoeba.plugin.storage.upyun.buckerName}")
    private String bucketName;

    @Value("${aoeba.plugin.storage.upyun.userName}")
    private String userName;

    @Value("${aoeba.plugin.storage.upyun.password}")
    private String password;

    @Value("${aoeba.plugin.storage.rootDir}")
    private String rootDir;

    @Value("${aoeba.plugin.storage.url}")
    private String url;

    @Value("${aoeba.plugin.storage.upyun.reqSecretKey}")
    private String reqSecretKey;

    @PostConstruct
    public void init() {
        restManager = new RestManager(bucketName, userName, password);
        if (rootDir == null || rootDir.equals("")) {
            rootDir = "/wwwStorage";
        }
    }

    @Override
    public Response mkDir(String path) throws Exception {
        okhttp3.Response res = restManager.mkDir(rootDir + path);
        return Response.builder().isSuccessful(res.isSuccessful()).msg(res.message()).build();
    }

    @Override
    public Response rmDir(String path) throws Exception {
        okhttp3.Response res = restManager.rmDir(rootDir + path);
        return Response.builder().isSuccessful(res.isSuccessful()).msg(res.message()).build();
    }

    @Override
    public Response readDir(String path) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RestManager.PARAMS.ACCEPT.getValue(), "application/json");
        okhttp3.Response res = restManager.readDirIter(rootDir + path, params);
        // {
        // "files": [{
        // "type": "image/jpeg",
        // "length": 4237,
        // "name": "foo.jpg",
        // "last_modified": 1415096225
        // }, {
        // "type": "folder",
        // "length": 0,
        // "name": "bar",
        // "last_modified": 1415096260
        // }],
        // "iter": "c2Rmc2Rsamdvc2pnb3dlam9pd2Vmd2Z3Zg=="
        // }
        Response.ResponseBuilder builder = Response.builder();
        builder.isSuccessful(res.isSuccessful()).msg(res.message());
        if (res.isSuccessful()) {
            GsonJsonParser gsonJsonParser = new GsonJsonParser();
            Map<String, Object> parseMap = gsonJsonParser.parseMap(res.body().string());
            List<Map<String, Object>> object = (List<Map<String, Object>>) parseMap.get("files");
            if (object.size() > 0) {
                object.forEach(x -> {
                    if ("folder".equals(x.get("type"))) {
                        x.put("isFolder", true);
                    } else {
                        x.put("isFolder", false);
                        x.put("url", url + path + "/" + x.get("name") + "!" + reqSecretKey);
                    }
                });
                builder.result(object);
            }
        }
        return builder.build();
    }

    @Override
    public Response writeFile(String filePath, MultipartFile file) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        // ???????????????????????? Content-MD5 ???
        // ???????????????????????????????????????MD5?????????????????????????????????????????? 406 NotAcceptable ??????
        // params.put(PARAMS.CONTENT_MD5.getValue(), UpYunUtils.md5(inputStream, 1024));

        // ????????????????????????"????????????"
        // ?????????
        // ???????????????????????????????????????????????????????????????URL?????????????????????URL???????????????????????????????????????+?????????????????????
        // ?????????
        // ?????????????????????????????????"!"????????????"bac"????????????????????????"/folder/test.jpg"???
        // ??????????????????????????????????????????http://???????????? /folder/test.jpg!bac
        params.put(RestManager.PARAMS.CONTENT_SECRET.getValue(), reqSecretKey);
        okhttp3.Response res = restManager.writeFile(rootDir + filePath, file.getInputStream(), params);
        String file_url = "";
        if (res.isSuccessful()) {
            file_url = url + filePath + "!" + reqSecretKey;
        }
        return Response.builder().isSuccessful(res.isSuccessful()).msg(res.message()).result(file_url).build();
    }

    @Override
    public Response readFile(String filePath) {
        // TODO Auto-generated method stub
        return Response.builder().isSuccessful(false).msg("?????????????????????").build();
    }

    @Override
    public Response deleteFile(String filePath) {
        // TODO Auto-generated method stub
        return Response.builder().isSuccessful(false).msg("?????????????????????").build();
    }

    @Override
    public Response moveFile(String from, String to) {
        // TODO Auto-generated method stub
        return Response.builder().isSuccessful(false).msg("?????????????????????").build();
    }

}

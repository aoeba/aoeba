package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.web.dto.BaseResponse;
import com.devsss.aoeba.web.dto.DirItem;
import com.devsss.aoeba.web.dto.RespCode;
import com.devsss.aoeba.web.plugin.storage.IStorage;
import com.devsss.aoeba.web.plugin.storage.Response;
import com.devsss.aoeba.web.utils.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.SequenceInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage")
public class StorageCtrl {

    @Autowired
    @Resource(name = "${aoeba.plugin.storage.name}")
    IStorage storage;

    @GetMapping("/mkdir")
    public BaseResponse<Object> mkDir(@RequestParam String path) {
        try {
            Response resp = storage.mkDir(path);
            if (resp.isSuccessful()) {
                return new BaseResponse<>(RespCode.OK, "成功", null);
            } else {
                return new BaseResponse<>(RespCode.ERROR, resp.getMsg(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
        }
    }

    @GetMapping("/rmdir")
    public BaseResponse<Object> rmDir(@RequestParam String path) {
        try {
            Response resp = storage.rmDir(path);
            if (resp.isSuccessful()) {
                return new BaseResponse<>(RespCode.OK, "成功", null);
            } else {
                return new BaseResponse<>(RespCode.ERROR, resp.getMsg(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
        }
    }

    @GetMapping("/readdir")
    public BaseResponse<List<DirItem>> readDir(@RequestParam String path) {
        try {
            Response resp = storage.readDir(path);
            if (resp.isSuccessful()) {
                Object result = resp.getResult();
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) result;
                if (mapList == null || mapList.size() == 0) {
                    return new BaseResponse<>(RespCode.OK, "成功", new ArrayList<>());
                }
                List<DirItem> dirItems = BeanUtil.mapListToBeanList(mapList, DirItem.class);
                return new BaseResponse<>(RespCode.OK, "成功", dirItems);
            } else {
                return new BaseResponse<>(RespCode.ERROR, resp.getMsg(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/writefile")
    public Mono<BaseResponse<Object>> writeFile(@RequestPart("file") FilePart file, @RequestPart("path") String path) {
        return file.content().map(b -> b.asInputStream(true)).reduce(SequenceInputStream::new).map(ins -> {
            LocalDate now = LocalDate.now();
            StringBuilder sb = new StringBuilder();
            if (path == null || path.equals("")) {
                sb.append("/").append(now.getYear()).append("/")
                        .append(now.getMonthValue() > 9 ? now.getMonthValue() : "0" + now.getMonthValue())
                        .append("/").append(file.filename());
            } else {
                sb.append(path).append("/").append(file.filename());
            }
            try {
                Response resp = storage.writeFile(sb.toString(), ins);
                if (resp.isSuccessful()) {
                    return new BaseResponse<>(RespCode.OK, "成功", resp.getResult());
                } else {
                    return new BaseResponse<>(RespCode.ERROR, resp.getMsg(), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
            }
        });
    }
}

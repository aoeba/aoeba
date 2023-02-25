package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.web.dto.*;
import com.devsss.aoeba.web.utils.BeanUtil;
import com.github.pagehelper.Page;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/note")
public class NoteCtrl {

    NoteService noteService;

    @PostMapping
    public BaseResponse<NoteEx> insert(@RequestBody NoteEx n) {
        Note note = noteService.save(n.toNote());
        return new BaseResponse<>(RespCode.OK, "保存成功", new NoteEx(note));
    }

    @PutMapping
    public BaseResponse<NoteEx> update(@RequestBody NoteEx n) {
        Note note = noteService.save(n.toNote());
        return new BaseResponse<>(RespCode.OK, "更新成功", new NoteEx(note));
    }

    @GetMapping("/{id}")
    public BaseResponse<NoteEx> findById(@PathVariable String id) {
        Note o = noteService.findNoteById(id);
        if (o == null) {
            return new BaseResponse<>(RespCode.ERROR, "未找到相关信息", null);
        }
        return new BaseResponse<>(RespCode.OK, "查询成功", new NoteEx(o));
    }

    @GetMapping("/find")
    public BaseResponse<PageObj<NoteEx>> findByTagsAndKeyword(@RequestParam String tags, @RequestParam String keyword, @RequestParam String category,
                                                              @RequestParam int pageNo, @RequestParam int pageSize) {
        List<String> tagArr = null;
        if (tags != null && !tags.equals("")) {
            String[] tagStrs = tags.split(",");
            tagArr = Arrays.stream(tagStrs).collect(Collectors.toList());
        }
        Page<Note> nPage = noteService.findNotesByTagsAndKeyword(tagArr, keyword, pageNo, pageSize, category);
        final PageObj<NoteEx> pageObj = new PageObj<>();
        pageObj.setSize(nPage.getPageSize());
        pageObj.setNumber(nPage.getPageNum());
        pageObj.setHasNext(nPage.getPageNum() < nPage.getPages());
        pageObj.setHasContent(nPage.getResult().size() > 0);
        pageObj.setTotalPages(nPage.getPages());
        pageObj.setTotal(nPage.getTotal());
        pageObj.setContent(new ArrayList<>());
        if (nPage.getResult().size() > 0) {
            nPage.getResult().forEach(x -> {
                pageObj.getContent().add(new NoteEx(x));
            });
        }

        return new BaseResponse<>(RespCode.OK, "查询成功", pageObj);
    }

    @GetMapping("/getTags")
    public BaseResponse<List<Tag>> getTags() throws Exception {
        List<Map<String, Object>> tagList = noteService.countTag();
        return new BaseResponse<>(RespCode.OK, "查询成功", BeanUtil.mapListToBeanList(tagList, Tag.class));
    }

    @GetMapping("/getCategories")
    public BaseResponse<List<Category>> getCategories() throws Exception {
        List<Map<String, Object>> categoryMapList = noteService.countCategory();
        return new BaseResponse<>(RespCode.OK, "查询成功", BeanUtil.mapListToBeanList(categoryMapList, Category.class));
    }

    @PostMapping("/updateMark")
    public BaseResponse<Integer> updateMark(@RequestBody NoteEx note) {
        int updateMarkById = noteService.updateMarkById(note.getId(), note.getMark());
        if (updateMarkById > 0) {
            return new BaseResponse<>(RespCode.OK, "更新成功", updateMarkById);
        }
        return new BaseResponse<>(RespCode.ERROR, "操作失败", 0);
    }
}

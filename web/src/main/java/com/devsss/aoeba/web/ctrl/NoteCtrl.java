package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.service.vo.PageInfo;
import com.devsss.aoeba.web.dto.*;
import com.devsss.aoeba.web.utils.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/note")
public class NoteCtrl {

    NoteService noteService;

    @PostMapping
    public Mono<BaseResponse<NoteEx>> insert(@RequestBody NoteEx n) {
        Mono<Note> noteMono = noteService.save(n.toNote());
        return noteMono.map(note -> new BaseResponse<>(RespCode.OK, "保存成功", new NoteEx(note)));
    }

    @PutMapping
    public Mono<BaseResponse<NoteEx>> update(@RequestBody NoteEx n) {
        Mono<Note> noteMono = noteService.save(n.toNote());
        return noteMono.map(note -> new BaseResponse<>(RespCode.OK, "更新成功", new NoteEx(note)));
    }

    @GetMapping("/{id}")
    public Mono<BaseResponse<NoteEx>> findById(@PathVariable String id) {
        Mono<Note> noteMono = noteService.findNoteById(id);
        return noteMono.map(note -> {
            if (note == null) {
                return new BaseResponse<>(RespCode.ERROR, "未找到相关信息", null);
            }
            return new BaseResponse<>(RespCode.OK, "查询成功", new NoteEx(note));
        });
    }

    @GetMapping("/find")
    public Mono<BaseResponse<PageObj<NoteEx>>> findByTagsAndKeyword(@RequestParam String tags, @RequestParam String keyword, @RequestParam String category,
                                                                    @RequestParam int pageNo, @RequestParam int pageSize) {
        List<String> tagArr = null;
        if (tags != null && !tags.equals("")) {
            String[] tagStrs = tags.split(",");
            tagArr = Arrays.stream(tagStrs).collect(Collectors.toList());
        }
        Mono<PageInfo<Note>> pageMono = noteService.findNotesByTagsAndKeyword(tagArr, keyword, pageNo, pageSize, category);

        return pageMono.flatMap(p -> {
            final PageObj<NoteEx> pageObj = new PageObj<>();
            pageObj.setContent(new ArrayList<>());
            pageObj.setSize(p.getPageSize());
            pageObj.setNumber(p.getPageNo());
            pageObj.setTotalPages(p.getPages());
            pageObj.setTotal(p.getTotal());
            pageObj.setHasContent(p.isHasContent());
            pageObj.setHasNext(p.isHasNext());
            p.getContent().forEach(x -> {
                pageObj.getContent().add(new NoteEx(x));
            });
            return Mono.just(new BaseResponse<>(RespCode.OK, "", pageObj));
        });
    }

    @GetMapping("/getTags")
    public Mono<BaseResponse<List<Tag>>> getTags() {
        Flux<Map<String, Object>> mapFlux = noteService.countTag();
        return mapFlux.collectList().map(maps -> {
            try {
                return new BaseResponse<>(RespCode.OK, "查询成功", BeanUtil.mapListToBeanList(maps, Tag.class));
            } catch (Exception e) {
                return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
            }
        });
    }

    @GetMapping("/getCategories")
    public Mono<BaseResponse<List<Category>>> getCategories() {
        return noteService.countCategory().collectList().map(maps -> {
            try {
                return new BaseResponse<>(RespCode.OK, "查询成功", BeanUtil.mapListToBeanList(maps, Category.class));
            } catch (Exception e) {
                return new BaseResponse<>(RespCode.ERROR, e.getMessage(), null);
            }
        });
    }

    @PostMapping("/updateMark")
    public Mono<BaseResponse<Long>> updateMark(@RequestBody NoteEx note) {
        return noteService.updateMarkById(note.getId(), note.getMark())
                .map(len -> new BaseResponse<>(RespCode.OK, "更新成功", len));
    }
}

package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.web.dto.*;
import com.devsss.aoeba.web.utils.BeanUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.time.temporal.ChronoField.*;

@RestController
@AllArgsConstructor
public class IndexCtrl {

    NoteService noteService;

    @GetMapping("/index")
    public Mono<BaseResponse<IndexData>> index() {
        final IndexData indexData = new IndexData();
        Mono<List<NoteEx>> top10NoteMono = noteService.findTop10ByCreateAtDesc().map(NoteEx::new).collectList().map(x -> {
            indexData.setNoteList(x);
            return x;
        });
        Mono<List<Map<String, Object>>> categoriesMono = noteService.countCategory().collectList().map(categories -> {
            try {
                indexData.setCategories(BeanUtil.mapListToBeanList(categories, Category.class));
            } catch (Exception e) {
                return new ArrayList<>();
            }
            return categories;
        });

        Mono<List<Map<String, Object>>> tagsMono = noteService.countTag().collectList().map(tags -> {
            try {
                indexData.setTags(BeanUtil.mapListToBeanList(tags, Tag.class));
            } catch (Exception e) {
                return new ArrayList<>();
            }
            return tags;
        });

        Mono<List<NoteEx>> swiperMono = noteService.queryNotesByMark(1).map(NoteEx::new).collectList().map(swiperNotes -> {
            indexData.setSwiperNote(swiperNotes);
            return swiperNotes;
        });
        return Flux.merge(top10NoteMono, categoriesMono, tagsMono, swiperMono)
                .then(Mono.fromCallable(() -> new BaseResponse<>(RespCode.OK, "", indexData)));
    }

    @GetMapping("/archives")
    public Mono<BaseResponse<Map<Integer,List<Archives>>>> archives() {
        return noteService.findNotesByTagsAndKeyword(null, "", 1, 999, "")
                .map(pageInfo -> {
                    Map<Integer, List<Archives>> map = new HashMap<>();
                    pageInfo.getContent().forEach(note -> {
                        NoteEx ex = new NoteEx();
                        ex.setTitle(note.getTitle());
                        ex.setId(note.getId());
                        ex.setCategory(note.getCategory());
                        Archives archives = new Archives(note.getCreatedAt().get(MONTH_OF_YEAR),
                                note.getCreatedAt().get(DAY_OF_MONTH), ex);
                        // 取年，将对应的记录装进去
                        if(map.containsKey(note.getCreatedAt().get(YEAR))){
                            map.get(note.getCreatedAt().get(YEAR)).add(archives);
                        } else {
                            List<Archives> list = new ArrayList<>();
                            list.add(archives);
                            map.put(note.getCreatedAt().get(YEAR), list);
                        }
                    });
                    return new BaseResponse<>(RespCode.OK, "查询成功", map);
        });
    }
}

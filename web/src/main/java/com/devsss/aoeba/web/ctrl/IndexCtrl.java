package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.web.dto.*;
import com.devsss.aoeba.web.utils.BeanUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/index")
public class IndexCtrl {

    NoteService noteService;

    @GetMapping
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
                throw new RuntimeException(e);
            }
            return categories;
        });

        Mono<List<Map<String, Object>>> tagsMono = noteService.countTag().collectList().map(tags -> {
            try {
                indexData.setTags(BeanUtil.mapListToBeanList(tags, Tag.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return tags;
        });

        Mono<List<NoteEx>> swiperMono = noteService.queryNotesByMark(1).map(NoteEx::new).collectList().map(swiperNotes -> {
            indexData.setSwiperNote(swiperNotes);
            return swiperNotes;
        });
        return Flux.merge(top10NoteMono, categoriesMono, tagsMono, swiperMono).then()
                .map(v -> new BaseResponse<>(RespCode.OK, "", indexData));
    }
}

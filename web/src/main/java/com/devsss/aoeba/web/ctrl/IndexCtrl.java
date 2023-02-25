package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.web.dto.*;
import com.devsss.aoeba.web.utils.BeanUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/index")
public class IndexCtrl {

    NoteService noteService;

    @GetMapping
    public BaseResponse<IndexData> index() throws Exception {
        final IndexData indexData = new IndexData();
        List<Note> n = noteService.findTop10ByCreateAtDesc();
        List<Map<String, Object>> tagMapList = noteService.countTag();
        ArrayList<NoteEx> noteList = new ArrayList<>();
        n.forEach(x -> {
            noteList.add(new NoteEx(x));
        });
        List<Map<String, Object>> categoryMapList = noteService.countCategory();
        List<Note> swiperNotes = noteService.queryNotesByMark(1);
        ArrayList<NoteEx> swiperNoteList = new ArrayList<>();
        swiperNotes.forEach(note -> {
            swiperNoteList.add(new NoteEx(note));
        });
        indexData.setNoteList(noteList);
        indexData.setTags(BeanUtil.mapListToBeanList(tagMapList, Tag.class));
        indexData.setCategories(BeanUtil.mapListToBeanList(categoryMapList, Category.class));
        indexData.setSwiperNote(swiperNoteList);
        return new BaseResponse<>(RespCode.OK, "", indexData);
    }
}

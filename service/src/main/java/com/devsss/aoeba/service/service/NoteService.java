package com.devsss.aoeba.service.service;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.domain.NoteTagDz;
import com.devsss.aoeba.service.mapper.NoteMapper;
import com.devsss.aoeba.service.mapper.NoteTagDzMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class NoteService {

    NoteMapper noteMapper;

    NoteTagDzMapper noteTagDzMapper;

    public Note save(Note note) {
        boolean isInsert = false;
        if (note.getId() == null || "".equals(note.getId())) {
            isInsert = true;
            // 新增
            note.setId(RandomStringUtils.randomAlphabetic(8));
            note.setCreatedAt(new Date());
            noteMapper.insert(note);
        } else {
            // 更新
            noteMapper.updateNoteInfoById(note);
        }
        // 更新note与tag对照表
        if (note.getTags() != null && !note.getTags().equals("")) {
            final ArrayList<NoteTagDz> delList = new ArrayList<>();
            final ArrayList<NoteTagDz> addList = new ArrayList<>();
            final List<String> nowTags = Arrays.stream(note.getTags().split(",")).map(String::toString)
                    .collect(Collectors.toList());
            if (isInsert) {
                nowTags.forEach(x -> {
                    NoteTagDz t = new NoteTagDz();
                    t.setTagName(x);
                    t.setNoteId(note.getId());
                    addList.add(t);
                });
            } else {
                List<NoteTagDz> noteTagDzs = noteTagDzMapper.selectListByNoteId(note.getId());
                List<NoteTagDz> del = noteTagDzs.stream().filter(x -> !nowTags.contains(x.getTagName())).collect(Collectors.toList());
                delList.addAll(del);
                List<String> noteTagNames = noteTagDzs.stream().map(NoteTagDz::getTagName).collect(Collectors.toList());
                nowTags.forEach(x -> {
                    if (!noteTagNames.contains(x)) {
                        NoteTagDz t = new NoteTagDz();
                        t.setTagName(x);
                        t.setNoteId(note.getId());
                        addList.add(t);
                    }
                });
            }
            if (delList.size() > 0) {
                noteTagDzMapper.deleteBatchIds(delList);
            }
            if (addList.size() > 0) {
                noteTagDzMapper.saveBatchByNative(addList);
            }
        } else {
            noteTagDzMapper.deleteById(note.getId());
        }

        return note;
    }

    public Note findNoteById(String id) {
        return noteMapper.selectNoteById(id);
    }

    public Page<Note> findNotesByTagsAndKeyword(List<String> tags, String keyword, int pageNo, int pageSize, String category) {
        PageHelper.startPage(pageNo, pageSize);
        List<Note> noteList = noteMapper.findByTagsAndKeyword(tags, keyword, category);
        Page<Note> nPage = (Page<Note>) noteList;
        log.debug("查询到note数量:" + nPage.getResult().size() + " 当前页:" + nPage.getPageNum() + " 总数:" + nPage.getTotal());
        return nPage;
    }

    public List<Map<String, Object>> countTag() {
        List<Map<String, Object>> tagMapList = noteTagDzMapper.countTag();
        log.debug("countTag:" + tagMapList.size());
        return tagMapList;
    }

    public List<Map<String, Object>> countCategory() {
        List<Map<String, Object>> categoryMapList = noteMapper.countCategory();
        log.debug("countCategory:" + categoryMapList.size());
        return categoryMapList;
    }

    public int updateMarkById(String id, int mark) {
        return noteMapper.updateMarkById(id, mark);
    }

    public List<Note> queryNotesByMark(int mark) {
        return noteMapper.queryNotesByMark(mark);
    }

    public List<Note> findTop10ByCreateAtDesc() {
        return noteMapper.findTop10ByCreateAtDesc();
    }
}

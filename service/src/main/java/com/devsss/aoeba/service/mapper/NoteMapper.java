package com.devsss.aoeba.service.mapper;

import com.devsss.aoeba.service.domain.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoteMapper {

    int updateNoteInfoById(@Param("note") Note note);

    Note selectNoteById(String id);

    int insert(Note note);

    List<Note> findByTagsAndKeyword(List<String> tags, String keyword, String category);

    List<Map<String, Object>> countCategory();

    int updateMarkById(String id, int mark);

    List<Note> queryNotesByMark(int mark);

    List<Note> findTop10ByCreateAtDesc();
}





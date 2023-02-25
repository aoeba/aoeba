package com.devsss.aoeba.service.mapper;

import com.devsss.aoeba.service.domain.NoteTagDz;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface NoteTagDzMapper {

    int saveBatchByNative(List<NoteTagDz> noteTagDzList);

    int deleteBatchIds(ArrayList<NoteTagDz> delList);

    List<NoteTagDz> selectListByNoteId(String id);

    int deleteById(String id);

    List<Map<String, Object>> countTag();

}





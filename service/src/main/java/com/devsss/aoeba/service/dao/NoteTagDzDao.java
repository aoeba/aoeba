package com.devsss.aoeba.service.dao;

import com.devsss.aoeba.service.domain.NoteTagDz;
import com.devsss.aoeba.service.vo.Tag;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public interface NoteTagDzDao extends R2dbcRepository<NoteTagDz, String> {

    @Query("select * from note_tag_dz where note_id = :id")
    Flux<NoteTagDz> selectListByNoteId(String id);

    @Modifying
    @Query("delete from note_tag_dz where note_id = :id")
    Mono<Void> deleteByNoteId(String id);

    @Query("select tag_name name,count(tag_name) size from note_tag_dz GROUP BY tag_name")
    Flux<Tag> countTag();
}

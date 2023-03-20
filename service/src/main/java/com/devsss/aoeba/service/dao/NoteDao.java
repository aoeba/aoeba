package com.devsss.aoeba.service.dao;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.vo.Category;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public interface NoteDao extends R2dbcRepository<Note, String> {

    @Query("select a.* from note a where  (0 = :byTags or a.id IN (select b.note_id from note_tag_dz b WHERE b.tag_name in (:tags)) ) " +
            "and (0 = :byKeyword or a.content like concat('%',:keyword,'%') or a.title like concat('%',:keyword,'%') ) " +
            "and (0 = :byCategory or a.category = :category) LIMIT :offset,:pageSize")
    Flux<Note> findByTagsAndKeyword(@Param("byTags") int byTags, @Param("tags") List<String> tags,
                                    @Param("byKeyword") int byKeyword, @Param("keyword") String keyword, @Param("byCategory") int byCategory,
                                    @Param("category") String category, int offset, int pageSize);

    @Query("select count(*) from note a where  (0 = :byTags or a.id IN (select b.note_id from note_tag_dz b WHERE b.tag_name in (:tags)) ) " +
            "and (0 = :byKeyword or a.content like concat('%',:keyword,'%') or a.title like concat('%',:keyword,'%') ) " +
            "and (0 = :byCategory or a.category = :category)")
    Mono<Long> count(@Param("byTags") int byTags, @Param("tags") List<String> tags,
                     @Param("byKeyword") int byKeyword, @Param("keyword") String keyword, @Param("byCategory") int byCategory,
                     @Param("category") String category);

    @Query("select category,count(id) size from note group by category")
    Flux<Category> countCategory();

    @Modifying
    @Query("update note n set n.mark = :mark where n.id = :id")
    Mono<Long> updateMarkById(String id, int mark);

    @Query("select * from note where mark = :mark")
    Flux<Note> queryNotesByMark(int mark);

    @Query("select * from note order by create_at desc limit 10")
    Flux<Note> findTop10ByCreateAtDesc();
}

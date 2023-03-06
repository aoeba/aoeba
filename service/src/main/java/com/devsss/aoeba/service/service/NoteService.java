package com.devsss.aoeba.service.service;

import com.devsss.aoeba.service.dao.NoteDao;
import com.devsss.aoeba.service.dao.NoteTagDzDao;
import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.domain.NoteTagDz;
import com.devsss.aoeba.service.vo.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class NoteService {

    NoteDao noteDao;

    NoteTagDzDao noteTagDzDao;

    DatabaseClient databaseClient;

    R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * 批量新增noteTagDz
     *
     * @param dzs 待新增列表
     * @return 结果
     */
    private Mono<Void> insertNoteTagDzBatch(List<NoteTagDz> dzs) {
        if (dzs.size() < 1) {
            return Mono.empty();
        }
        return databaseClient.sql("insert into note_tag_dz (note_id,tag_name,id) values ( ?, ?,?)").filter(statement -> {
            dzs.forEach(noteTagDz -> {
                statement.bind(0, noteTagDz.getNoteId()).bind(1, noteTagDz.getTagName()).bind(2, noteTagDz.getId()).add();
            });
            return statement;
        }).then();
    }

    /**
     * 批量删除noteTagDz
     *
     * @param dzs 待删除列表
     * @return 结果
     */
    private Mono<Void> deleteNoteTagDzBatch(List<NoteTagDz> dzs) {
        if (dzs.size() < 1) {
            return Mono.empty();
        }
        return databaseClient.sql("delete from note_tag_dz where note_id = ? and tag_name = ?").filter(statement -> {
            dzs.forEach(noteTagDz -> {
                statement.bind(0, noteTagDz.getNoteId()).bind(1, noteTagDz.getTagName()).add();
            });
            return statement;
        }).then();
    }

    /**
     * 更新或新增note
     *
     * @param note 待处理note
     * @return 操作结果
     */
    public Mono<Note> save(Note note) {
        // TODO Transaction does not work in MySQL environment #250:https://github.com/mirromutth/r2dbc-mysql/issues/250
        boolean isInsert = false;
        Mono<Note> noteMono;
        if (note.getId() == null || "".equals(note.getId())) {
            isInsert = true;
            // 新增
            note.setId(RandomStringUtils.randomAlphabetic(8));
            note.setCreatedAt(LocalDateTime.now());
            note.setViews(0);
            noteMono = r2dbcEntityTemplate.insert(note);
        } else {
            noteMono = r2dbcEntityTemplate.update(note);
        }
        // 更新note与tag对照表
        if (note.getTags() != null && !note.getTags().equals("")) {
            final ArrayList<NoteTagDz> delList = new ArrayList<>();
            final ArrayList<NoteTagDz> addList = new ArrayList<>();
            final List<String> nowTags = Arrays.stream(note.getTags().split(",")).map(String::toString)
                    .collect(Collectors.toList());
            if (isInsert) {
                nowTags.forEach(x -> {
                    addList.add(new NoteTagDz(note.getId(), x));
                });
                Mono<Void> saveMono = insertNoteTagDzBatch(addList);
                return Mono.zip(noteMono, saveMono, (t1, t2) -> t1);
            } else {
                Flux<NoteTagDz> noteTagDzMono = noteTagDzDao.selectListByNoteId(note.getId());
                Mono<Void> addTagDzMono = noteTagDzMono
                        .map(NoteTagDz::getTagName).collectList().defaultIfEmpty(Collections.singletonList("")).flatMap(dzs -> {
                            dzs.forEach(dz -> {
                                if (!nowTags.contains(dz)) {
                                    log.debug("获取待删除的noteTagDz:{}-{}", note.getId(), dz);
                                    delList.add(new NoteTagDz(note.getId(), dz));
                                }
                            });
                            nowTags.forEach(x -> {
                                if (!dzs.contains(x)) {
                                    log.debug("获取待新增的noteTagDz:{}-{}", note.getId(), x);
                                    addList.add(new NoteTagDz(note.getId(), x));
                                }
                            });
                            Mono<Void> delMono = deleteNoteTagDzBatch(delList);
                            Mono<Void> saveMono = insertNoteTagDzBatch(addList);
                            return Flux.merge(delMono, saveMono).then();
                        });
                return Mono.zip(noteMono, addTagDzMono, (t1, t2) -> t1).defaultIfEmpty(note).map(n -> n);
            }
        } else {
            Mono<Void> delMono = noteTagDzDao.deleteByNoteId(note.getId());
            return Mono.zip(noteMono, delMono, (t1, t2) -> t1).defaultIfEmpty(note).map(n -> n);
        }
    }

    /**
     * 根据note id查询note
     *
     * @param id note id
     * @return 结果
     */
    public Mono<Note> findNoteById(String id) {
        return noteDao.findById(id);
    }

    /**
     * 根据标签、关键字、分类查询note（分页）
     *
     * @param tags     标签列表
     * @param keyword  关键字
     * @param pageNo   当前页数
     * @param pageSize 每页条数
     * @param category 分类
     * @return 查询结果
     */
    public Mono<PageInfo<Note>> findNotesByTagsAndKeyword(List<String> tags, String keyword, int pageNo,
                                                          int pageSize, String category) {
        Flux<Note> noteFlux = noteDao.findByTagsAndKeyword(tags == null || tags.size() == 0 ? 0 : 1, tags,
                Objects.equals(keyword, "") ? 0 : 1,
                keyword, Objects.equals(category, "") ? 0 : 1, category, pageSize * (pageNo - 1), pageSize);
        Mono<Long> countMono = noteDao.count();
        PageInfo<Note> pageInfo = new PageInfo<>();
        return Mono.zip(countMono, noteFlux.collect(Collectors.toList()), (total, noteList) -> {
            pageInfo.setTotal(total);
            pageInfo.setPages((int) ((total + pageSize - 1) / pageSize));
            pageInfo.setPageNo(pageNo);
            pageInfo.setPageSize(pageSize);
            pageInfo.setHasContent(noteList.size() > 0);
            pageInfo.setHasNext(pageInfo.getPages() > pageNo);
            pageInfo.setContent(noteList);
            return pageInfo;
        });
    }

    /**
     * 统计tag数量
     *
     * @return [{name:'',size:''}]
     */
    public Flux<Map<String, Object>> countTag() {
        // 返回值使用map接收时返回的是 note属性组成的map,故使用对象接收再转成map
        return noteTagDzDao.countTag().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("name", c.getName());
            m.put("size", c.getSize());
            return m;
        });
    }

    /**
     * 统计分类数量
     *
     * @return [{category:'',size:''}]
     */
    public Flux<Map<String, Object>> countCategory() {
        return noteDao.countCategory().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("category", c.getCategory());
            m.put("size", c.getSize());
            return m;
        });
    }

    /**
     * 根据note id更新mark
     *
     * @param id   note id
     * @param mark 更新后mark
     * @return 影响记录
     */
    public Mono<Long> updateMarkById(String id, int mark) {
        return noteDao.updateMarkById(id, mark);
    }

    /**
     * 根据mark查询note
     *
     * @param mark mark值
     * @return 查询结果
     */
    public Flux<Note> queryNotesByMark(int mark) {
        return noteDao.queryNotesByMark(mark);
    }

    /**
     * 查询最近10条note
     *
     * @return 最近10条note
     */
    public Flux<Note> findTop10ByCreateAtDesc() {
        return noteDao.findTop10ByCreateAtDesc();
    }
}

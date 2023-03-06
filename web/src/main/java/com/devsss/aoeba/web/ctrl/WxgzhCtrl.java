package com.devsss.aoeba.web.ctrl;

import com.devsss.aoeba.service.domain.Note;
import com.devsss.aoeba.service.service.NoteService;
import com.devsss.aoeba.web.dto.BaseResponse;
import com.devsss.aoeba.web.dto.NoteEx;
import com.devsss.aoeba.web.dto.RespCode;
import com.devsss.aoeba.web.plugin.wxgzh.HtmlUtils;
import com.devsss.aoeba.web.plugin.wxgzh.MaterialType;
import com.devsss.aoeba.web.plugin.wxgzh.WxgzhApi;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/wxgzh")
public class WxgzhCtrl {

    WxgzhApi wxgzhApi;

    NoteService noteService;

    @PostMapping("/push")
    public BaseResponse<Object> pushNote(@RequestBody NoteEx note) throws Exception {
        if (note.getImg() == null || "".equals(note.getImg())) {
            return new BaseResponse<>(RespCode.ERROR, "推送到微信时，需要封面图片", null);
        }
//        Note noteById = noteService.findNoteById(note.getId());
//        if (noteById != null) {
//            String filePath = HtmlUtils.downFile(note.getImg(), "/wwwtemp", null);
//            String[] material = wxgzhApi.addMaterial(MaterialType.image, filePath);
//            String wxDraft = wxgzhApi.html2WxDraft(noteById.getContent());
//            String draftId = wxgzhApi.addDraft(noteById.getTitle(), null, wxDraft,
//                    "https://www.mk95.cn/note/" + noteById.getId(), material[0]);
//            // 推送到草稿即可，部分格式会被微信抹去，且无法群发
//            // wxgzhApi.freePublishDraft(draftId);
//        } else {
//            return new BaseResponse<>(RespCode.ERROR, "未找到相关NOTE,NOTE_ID:" + note.getId(), null);
//        }
        return new BaseResponse<>(RespCode.OK, "推送成功", null);
    }
}

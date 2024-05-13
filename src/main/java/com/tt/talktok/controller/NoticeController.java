package com.tt.talktok.controller;

import com.tt.talktok.dto.NoticeDto;
import com.tt.talktok.dto.ReviewDto;
import com.tt.talktok.entity.Notice;
import com.tt.talktok.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 리스트
    @GetMapping("/list") //아무 설정 없이 @PageableDefault 어노테이션을 사용시, default 설정으로 페이징 기법이 발생.
    public String noticeList(@PageableDefault(page = 0, size = 10, sort = "noNo", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model, String keyword){
        Page<NoticeDto> list = null;
        if(keyword == null){
            list = noticeService.getNoticePage(pageable);
        }else{ //검색어 있을 시
            list = noticeService.getNoticeSearchPage(keyword, pageable);
        }
        model.addAttribute("list", list);
        return "notice/notice";
    }
    // 공지사항 작성폼으로 이동
    @GetMapping("/write")
    public String noticeWrite() {
        return "notice/writeForm";
    }

    // 공지사항 작성후 db 저장
    @PostMapping("/write")
    public String write(NoticeDto noticeDto) {
        noticeService.noticeWrite(noticeDto);
        return "notice/writeMessage";
    }

    // 공지사항 상세 정보 출력
    @GetMapping("/detail")
    public String noticeDetail(@RequestParam("noNo") int noNo, @RequestParam int page, Model model) {
        noticeService.updateCount(noNo);
        Notice noticeDetail = noticeService.getNoticeDetail(noNo);
        model.addAttribute("notice", noticeDetail);
        model.addAttribute("page", page);
        return "notice/detail";
    }

    // 공지사항 수정폼 이동
    @GetMapping("/update")
    public String noticeUpdate(@RequestParam("noNo") int noNo, Model model){
        Notice notice = noticeService.getNoticeDetail(noNo);
        model.addAttribute("notice", notice);
        return "notice/updateForm";
    }

    // 공지사항 수정 후 db저장
    @PostMapping("/update")
    public String update(@RequestParam(value = "page", defaultValue = "1") int page, NoticeDto noticeDto){
        noticeService.noticeUpdate(noticeDto);
        return "notice/updateMessage";
    }

    // 공지사항 삭제 컨펌
    @GetMapping("/delete")
    public String checkDelete(@RequestParam("noNo") int noNo, Model model){
        model.addAttribute("noNo", noNo);
        return "notice/deleteMessage";
    }

    // 공지사항 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam("noNo") int noNo, Pageable pageable){
        noticeService.delete(noNo);
        System.out.println("pn : "+ pageable.getPageNumber());
        return "redirect:list?page=0";
    }

}

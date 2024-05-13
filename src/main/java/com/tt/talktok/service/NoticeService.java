package com.tt.talktok.service;

import com.tt.talktok.dto.NoticeDto;
import com.tt.talktok.entity.Notice;
import com.tt.talktok.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 메인 리스트
    public List<NoticeDto> findAllMain() {
        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지, 5개씩
        List<Notice> list = noticeRepository.findAllByOrderByNoDateDesc(pageable);
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    //상세, 페이징
    public Page<NoticeDto> getNoticePage(Pageable pageable) {
        Page<Notice> list = noticeRepository.findAll(pageable);
        return list.map(this::convertToDto);
    }

    //검색
    public Page<NoticeDto> getNoticeSearchPage(String keyword, Pageable pageable){
        Page<Notice> list = noticeRepository.findByNoSubjectContaining(keyword, pageable);
        return list.map(this::convertToDto);
    }

    public NoticeDto convertToDto(Notice notice) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNoNo(notice.getNoNo());
        noticeDto.setNoSubject(notice.getNoSubject());
        if(noticeDto.getNoContent() != null){
            noticeDto.setNoContent(noticeDto.getNoContent().replace("\\n","<br/>"));};
        noticeDto.setNoReadcount(notice.getNoReadcount());
        noticeDto.setNoDate(notice.getNoDate());

        return noticeDto;
    }

    //작성
    public void noticeWrite(NoticeDto noticeDto){
        LocalDateTime currentDateTime = LocalDateTime.now();
        Notice notice = Notice.builder()
                        .noSubject(noticeDto.getNoSubject())
                        .noContent(noticeDto.getNoContent())
                        .noDate(currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build();

        noticeRepository.save(notice);
    }

    //상세
    public Notice getNoticeDetail(Integer noNo) {
        return noticeRepository.findById(noNo).orElse(null);
    }

    @Transactional
    public void updateCount(Integer noNo) {
        noticeRepository.updateCount(noNo);
    }

    //수정
    public void noticeUpdate(NoticeDto noticeDto) {
        Notice notice = Notice.builder()
                        .noNo(noticeDto.getNoNo())
                        .noSubject(noticeDto.getNoSubject())
                        .noContent(noticeDto.getNoContent())
                        .noDate(noticeDto.getNoDate())
                        .noReadcount(noticeDto.getNoReadcount())
                        .build();

        System.out.println(notice.getNoSubject());

        noticeRepository.save(notice);
    }

    //삭제
    public void delete(int noNo) {
        noticeRepository.deleteById(noNo);
    }
}

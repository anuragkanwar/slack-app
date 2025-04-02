package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.dto.common.InviteDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateInviteRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InviteService {

    @Transactional
    List<InviteDto> findInvitesByInviteeAndStatus_Pending();

    void accept(Long inviteId);

    void reject(Long inviteId);

    void save(CreateInviteRequestDto requestDto);
}

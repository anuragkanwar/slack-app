package com.anuragkanwar.slackmessagebackend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateInviteRequestDto {
    private List<Long> inviteeIds;
    private String workspaceId;
}

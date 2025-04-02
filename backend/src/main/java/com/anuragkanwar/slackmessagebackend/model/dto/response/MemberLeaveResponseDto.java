package com.anuragkanwar.slackmessagebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLeaveResponseDto {
    private Long roomId;
    private Long userId;
}

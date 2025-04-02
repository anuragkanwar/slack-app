package com.anuragkanwar.slackmessagebackend.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceChangeDto {
    private Long userId;
    private Boolean isOnline;
}

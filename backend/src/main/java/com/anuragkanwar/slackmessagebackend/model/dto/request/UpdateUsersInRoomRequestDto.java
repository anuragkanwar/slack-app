package com.anuragkanwar.slackmessagebackend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUsersInRoomRequestDto {
    private Set<Long> userIds;
}

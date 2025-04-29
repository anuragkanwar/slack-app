package com.anuragkanwar.slackmessagebackend.model.dto.response;

import com.anuragkanwar.slackmessagebackend.model.dto.common.WorkspaceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String email;
    private String wsUrl;
    private Set<WorkspaceDto> workspaces;
    private String token;
}
package com.anuragkanwar.slackmessagebackend.model.dto.common;

import com.anuragkanwar.slackmessagebackend.model.domain.Invite;
import com.anuragkanwar.slackmessagebackend.model.enums.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteDto {

    private Long id;
    private UserDto inviter;
    private UserDto invitee;
    private WorkspaceDto workspace;
    private InviteStatus status;


    public static InviteDto toDto(Invite invite) {
        return InviteDto.builder()
                .id(invite.getId())
                .inviter(UserDto.ToDtoSmall(invite.getInviter()))
                .invitee(UserDto.ToDtoSmall(invite.getInvitee()))
                .workspace(WorkspaceDto.toDtoSmall(invite.getWorkspace()))
                .status(invite.getStatus())
                .build();
    }


    public static InviteDto toDtoSmall(Invite invite) {
        return toDto(invite);
    }


    public static Set<InviteDto> inviteSetToInviteDtoSet(Set<Invite> invites) {
        return invites.stream().map(InviteDto::toDtoSmall).collect(Collectors.toSet());
    }


}
package com.anuragkanwar.slackmessagebackend.controller;

import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateInviteRequestDto;
import com.anuragkanwar.slackmessagebackend.service.InviteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invite")
@Slf4j
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @GetMapping
    public ResponseEntity<?> findInvitesByInviteeAndStatus_Pending() {
        return ResponseEntity.ok().
                body(inviteService.findInvitesByInviteeAndStatus_Pending());
    }

    @PostMapping
    public ResponseEntity<?> createInvite(@RequestBody CreateInviteRequestDto requestDto) {
        inviteService.save(requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inviteId}/accept")
    public ResponseEntity<?> acceptInvite(@PathVariable Long inviteId) {
        inviteService.accept(inviteId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inviteId}/reject")
    public ResponseEntity<?> rejectInvite(@PathVariable Long inviteId) {
        inviteService.reject(inviteId);
        return ResponseEntity.ok().build();
    }
}

package com.anuragkanwar.slackmessagebackend.controller;

import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateWorkspaceRequestDto;
import com.anuragkanwar.slackmessagebackend.service.WorkspaceService;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    final WorkspaceService workspaceService;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }


    @GetMapping
    public ResponseEntity<?> getWorkspacesByUserId() {
        return ResponseEntity.ok()
                .body(workspaceService.getWorkspacesByUserId());
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<?> getWorkspacesById(@PathVariable Long workspaceId) {

        String workspaceIdEncoded = Utils.encodeToBase64(workspaceId.toString());
        ResponseCookie cookie = ResponseCookie
                .from("workspace-id", workspaceIdEncoded)
                .httpOnly(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        // SEND HELLO
        // SEND EVENT BOOT_*

        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header("X-workspace-id", workspaceIdEncoded)
                .body(workspaceService.getWorkspacesById(workspaceId));
    }

    @PostMapping
    public ResponseEntity<?> createWorkspace(@RequestBody CreateWorkspaceRequestDto workspace) {
        workspaceService.save(workspace);
        return ResponseEntity.ok().build();
    }

    // DO I HAVE TO IMPLEMENT INVITES TABLE AS WELL 0_0
    // or just creator of that workspace can pull anyone in o_O
    @PatchMapping("/addUsers")
    public ResponseEntity<?> addUserToWorkspace() {
        return ResponseEntity.ok().build();
    }

}

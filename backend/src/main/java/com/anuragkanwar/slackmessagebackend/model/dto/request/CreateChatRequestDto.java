package com.anuragkanwar.slackmessagebackend.model.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateChatRequestDto {
    private Long roomId;
    private String message;
    private Long parentId;
}

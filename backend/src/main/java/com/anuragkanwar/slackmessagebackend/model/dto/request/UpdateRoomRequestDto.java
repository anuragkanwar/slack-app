package com.anuragkanwar.slackmessagebackend.model.dto.request;


import com.anuragkanwar.slackmessagebackend.model.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoomRequestDto {

    private String name;
    private String description;
}

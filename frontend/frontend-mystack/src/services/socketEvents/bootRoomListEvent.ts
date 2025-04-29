import type { RoomType, UserDtoSmall } from "@/types";
import type { BaseEvent } from "./baseEvent";


export type RoomDtoSmall = {
  id: number
  name: string
  description: string
  roomType: RoomType
  creator: UserDtoSmall
}
export interface BootRoomListEvent extends BaseEvent<RoomDtoSmall[]> {
  
}
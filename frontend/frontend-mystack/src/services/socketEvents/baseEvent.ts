import type { UserDtoSmall } from "@/types";
import { SocketEvent } from "../SocketEventsEnum"
import type { RoomDtoSmall } from "./bootRoomListEvent"

export interface BaseEvent<T> {
  eventType: SocketEvent
  data: T
}

export type EventPayloads = {
  [SocketEvent.boot_user_list]: UserDtoSmall[]; // Example: Define payload for user list
  [SocketEvent.boot_room_list]: RoomDtoSmall[]; // Example: Define payload for user list
  [SocketEvent.room_created]: RoomDtoSmall;
  [SocketEvent.room_deleted]: { roomId: number }; // Example: Define payload for room deletion
  [SocketEvent.room_joined]: { roomId: number; userId: number }; // Example
  [SocketEvent.room_left]: { roomId: number; userId: number }; // Example
  [SocketEvent.room_rename]: { roomId: number; newName: string }; // Example
  [SocketEvent.goodbye]: void; // Example: Use 'void' if an event has no data payload
  [SocketEvent.hello]: { message: string }; // Example
  [SocketEvent.manual_presence_change]: { userId: number; status: string }; // Example
  [SocketEvent.member_joined_room]: { roomId: number; user: UserDtoSmall }; // Example
  [SocketEvent.member_left_room]: { roomId: number; userId: number }; // Example
  [SocketEvent.message]: { /* Define message payload type */ messageId: string; text: string; senderId: number; roomId: number }; // Example
  [SocketEvent.presence_change]: { userId: number; isOnline: boolean }; // Example
  [SocketEvent.typing]: { roomId: number; userId: number; isTyping: boolean }; // Example
  [SocketEvent.user_typing]: { roomId: number; userId: number; isTyping: boolean }; // Example (maybe same as typing?)
  [SocketEvent.user_joined_workspace]: { user: UserDtoSmall }; // Example
  [SocketEvent.user_left_workspace]: { userId: number }; // Example

  // ... make sure every single event from SocketEvent enum is covered
};
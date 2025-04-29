import type { QueryClient } from "@tanstack/react-query"
import { SocketEvent } from "../SocketEventsEnum"
import type { EventPayloads } from "../socketEvents/baseEvent"
import type { ChatDto } from "@/api/chats"
import type { ChatHistoryResponseDto } from "@/api/room"

export function handleSocketEvent<T extends SocketEvent>(
  eventName: T,
  data: EventPayloads[T],
  queryClient: QueryClient,
  workspaceId: string) {
  console.log(eventName, data, queryClient, workspaceId)
  switch (eventName) {
    case SocketEvent.message:
      let messageData = data as ChatDto
      queryClient.setQueryData(["room", `${messageData.room.id}` ,"history"], (prv : ChatHistoryResponseDto )=>{
          let newData: ChatHistoryResponseDto = {} as ChatHistoryResponseDto;
          newData.cursor = prv.cursor;
          newData.chats = [...prv.chats, messageData]
          return newData;
      })
      break
    default:
      queryClient.setQueryData(["ws", eventName], () => data)
  }
}
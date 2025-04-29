import { apiClient } from "@/lib/api-client"
import type { RoomDtoSmall } from "@/services/socketEvents/bootRoomListEvent"
import type { UserDtoSmall } from "@/types"
import { useMutation } from "@tanstack/react-query"

export enum ChatType {
  SERVER,
  USER,
}

export type ChatDto = {
  id: number
  message: string
  chatType : ChatType
  parent: ChatDto
  user : UserDtoSmall
  room : RoomDtoSmall
}

export type CreateChatRequestDto = {
  roomId: number
  message: string
  parentId?: string
}

const postChat = async (data: CreateChatRequestDto) => {
  const response = await apiClient.post('/chat', data);
  return response.data
}


const deleteChat = async (chatId: number) => {
  const response = await apiClient.delete(`/chat/${chatId}`);
  return response.data
}

export type UpdateChatRequestDto = {
  message: string
}

const updateChat = async (chatId: number, data: UpdateChatRequestDto) => {
  const response = await apiClient.patch(`/chat/${chatId}`, data);
  return response.data
}


export const usePostChat = () => {
  return useMutation({
    mutationKey: ["chat", "post"],
    mutationFn: postChat,
  })
}

export const useDeleteChat = () => {
  return useMutation({
    mutationKey: ["chat", "delete"],
    mutationFn: deleteChat,
  })
}

export const useUpdateChat = () => {
  return useMutation({
    mutationKey: ["chat", "update"],
    mutationFn: ({ chatId, data }: { chatId: number; data: UpdateChatRequestDto }) => updateChat(chatId, data),
  })
}

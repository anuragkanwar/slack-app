import { apiClient } from "@/lib/api-client";
import type { RoomType } from "@/types";
import { useMutation, useQuery } from "@tanstack/react-query";
import type { ChatDto } from "./chats";

export type CreateRoomRequestDto = {
  name: string
  description: string
  roomType: RoomType
  workspaceId: string
  users: number[]
}
const createRoom = async(data : CreateRoomRequestDto) => {
  const response = await apiClient.post("/room", data)
  return response.data
}

export const useCreateRoom = () => {
  return useMutation({
    mutationFn: createRoom
  })
}

const getRoomById = async(id : string) => {
  const response = await apiClient.get(`/room/${id}`)
  return response.data
}


export const useGetRoomById = (id : string) => {
  return useQuery({
    queryKey: ["room", id],
    queryFn: () => getRoomById(id)
  })
}

const deleteRoomById = async(id : string) => {
  const response = await apiClient.delete(`/room/${id}`)
  return response.data
}


export const useDeleteRoomById = () => {
  return useMutation({
    mutationFn: deleteRoomById
  })
}


export type UpdateRoomRequestDto = {
  name : string
  description : string
}

const updateRoom = async({ id, data }: { id: string; data: UpdateRoomRequestDto }) => {
  const response = await apiClient.patch(`/room/${id}`, data)
  return response.data
}

export const useUpdateRoom = () => {
  return useMutation({
    mutationFn: updateRoom
  })
}

export type RoomConverstaionParams = {
  cursor?: string
  limit: number
}

export type ChatHistoryResponseDto = {
  chats: ChatDto[]
  cursor: string
}

const getRoomConversations = async(id : string, params : RoomConverstaionParams) => {
  const response = await apiClient.get<ChatHistoryResponseDto>(`/room/${id}/history`, {
    params: { ...params, limit: params.limit ?? 10 }
  })
  return response.data
}

export const useGetRoomConversations = (id : string, params : RoomConverstaionParams) => {
  return useQuery({
    queryKey: ["room", id, "history"],
    queryFn: () => getRoomConversations(id, params)
  })
}


const updateUsersInRoom = async({ id, users }: { id: string; users: number[] }) => {
  const response = await apiClient.patch(`/room/${id}/users`, { users })
  return response.data
}

export const useUpdateUsersInRoom = () => {
  return useMutation({
    mutationFn: updateUsersInRoom
  })
}



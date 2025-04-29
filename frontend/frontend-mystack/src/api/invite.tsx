import { apiClient } from "@/lib/api-client";
import { useMutation, useQuery } from "@tanstack/react-query";

export type CreateInviteRequestDto = {
  inviteeIds: number[];
  workspaceId: string;
}

const createInvite = async(data : CreateInviteRequestDto) => {
  const response = await apiClient.post("/invite", data);
  return response.data;
}

const acceptInvite = async(inviteId : number) => {
  const response = await apiClient.post(`/invite/${inviteId}/accept`);
  return response.data;
}

const rejectInvite = async(inviteId : number) => {
  const response = await apiClient.post(`/invite/${inviteId}/reject`);
  return response.data;
}

const getInvite = async() => {
  const response = await apiClient.get("/invite");
  return response.data;
}


export const useGetInvites = () => {
  return useQuery({
    queryKey: ["invite"],
    queryFn: getInvite,
  });
}

export const useCreateInvite = () => {
  return useMutation({
    mutationFn: createInvite,
  });
}

export const useAcceptInvite = () => {
  return useMutation({
    mutationFn: acceptInvite,
  });
}

export const useRejectInvite = () => {
  return useMutation({
    mutationFn: rejectInvite,
  });
}
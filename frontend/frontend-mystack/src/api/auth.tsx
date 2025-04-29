import * as z from "zod";
import { apiClient } from "../lib/api-client";
import axios, { AxiosError } from "axios";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate, type UseNavigateResult } from "@tanstack/react-router";
import type { WorksapceDtoSmall } from "./workspace";


export const UserSchema = z.interface({
  id: z.number(),
  username: z.string(),
  email: z.string(),
  wsUrl : z.string()
})

export const SignupSchema = z.interface({
  username: z.string().min(3),
  email: z.email(),
  password : z.string().min(3)
})

export const LoginSchema = z.interface({
  username: z.string().min(3),
  password : z.string().min(3)
})

export type LoginRequestDto = z.infer<typeof LoginSchema>
export type SignupRequestDto = z.infer<typeof SignupSchema>
export type User = z.infer<typeof UserSchema> & {
  workspaces: WorksapceDtoSmall[],
  avatarUrl : string
  token: string
}

export const getUser = async() : Promise<User | null> =>{
  try{
    const response = await apiClient.get<User>("/auth/getMe");
    return response.data
  }catch(e : any){
    if(axios.isAxiosError(e)){
      const axiosError = e as AxiosError;
      if (axiosError.response?.status === 404){
        return null;
      }
    }
    throw e
  }
}

export const logout = (): Promise<void> => {
  return apiClient.post('/auth/logout');
};

const register = async (data: SignupRequestDto): Promise<User> => {
  const response = await apiClient.post<User>('/auth/signup', data);
  return response.data;
};

const login = async (data:LoginRequestDto): Promise<User> => {
  const response = await apiClient.post<User>('/auth/login', data);
  return response.data;
}

export const useUser = ()=>{
  const {data: user, status, error, isLoading, isFetching } = useQuery<User | null, Error>({
    queryKey: ["auth", "me"],
    queryFn: getUser,
  })
  if(user){
    user.token = localStorage.getItem("token") || ""
  }
  
  return {
    user,
    isAuthenticated: !!user && status === 'success',
    isLoading,
    isFetching,
    error
  };
}

const decideNavigate = (navigate: UseNavigateResult<string>)=>{
  const workspaceId = localStorage.getItem("workspaceId");
      if(workspaceId === null){
        navigate({to: "/workspace/select"})
      }else{
        navigate({to: `/workspace/${workspaceId}/chat`})
      }
}

export const useSignup = ()=>{
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: register,
    onSuccess: ({token})=>{
      console.log("user signup success");
      queryClient.invalidateQueries({queryKey: ["auth", "me"]})
      decideNavigate(navigate)
      localStorage.setItem("token", token)
    },
    onError: (e)=>{
      console.log("error in signup call", e);
    }
  })
}

export const useLogin = ()=>{
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: login,
    onSuccess: ({token})=>{
      console.log("user login success");
      queryClient.invalidateQueries({queryKey: ["auth", "me"]})
      decideNavigate(navigate)
      localStorage.setItem("token", token)
    },
    onError: (e)=>{
      console.log("error in login call", e);
    }
  })  
}


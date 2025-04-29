import { useUser } from "@/api/auth";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupAction,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { useLocalStorage } from "@/hooks/use-localstorage";
import type { EventPayloads } from "@/services/socketEvents/baseEvent";
import { SocketEvent } from "@/services/SocketEventsEnum";
import { useQuery } from "@tanstack/react-query"
import { useNavigate } from "@tanstack/react-router";
import { ChevronDown, Plus } from "lucide-react";

export function AppSidebar() {
  const { data } = useQuery<EventPayloads[SocketEvent.boot_room_list]>({
    queryKey: ["ws", SocketEvent.boot_room_list.toString()],
    initialData: [],
    queryFn: () => {
      return Promise.resolve([])
    },
    staleTime: Infinity,
    gcTime: Infinity
  })
  const [workspace, setWorkspace] = useLocalStorage<string | null>("workspaceId", null)
  const { user } = useUser();
  const naivgate = useNavigate();

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <SidebarMenuButton>
                  Select Workspace
                  <ChevronDown className="ml-auto" />
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-[--radix-popper-anchor-width]">
                {user?.workspaces?.map(workspace => (
                  <DropdownMenuItem key={workspace.id} onClick={() => {
                    setWorkspace(workspace.id)
                    naivgate({ to: `/workspace/${workspace.id}/chat` })
                  }}>
                    {workspace.name}
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Channels</SidebarGroupLabel>
          <SidebarGroupAction title="Add Channel">
            <Plus onClick={() => {
              console.log("Add Channel");
            }} /> <span className="sr-only">Add Channel</span>
          </SidebarGroupAction>
          <SidebarGroupContent>
            <SidebarMenu>
              {data.map(x =>
              (<SidebarMenuItem key={x.id}>
                <SidebarMenuButton onClick={() => {
                  naivgate({ to: `/workspace/${workspace}/chat/${x.id}` })
                }} >{x.name}</SidebarMenuButton>
              </SidebarMenuItem>)
              )}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter />
    </Sidebar>
  )
}

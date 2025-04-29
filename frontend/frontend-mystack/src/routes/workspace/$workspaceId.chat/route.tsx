import type { User } from '@/api/auth';
import { AppSidebar } from '@/components/layout/sidebar';
import { SidebarProvider } from '@/components/ui/sidebar';
import { SocketProvider } from '@/contexts/SocketContext'
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/workspace/$workspaceId/chat')({
  loader: async ({ context }) => {
    const { queryClient } = context;
    const user = queryClient.getQueryData<User>(["auth", "me"]);
    if (!user || !user.wsUrl) {
      redirect({
        to: "/workspace/select",
        replace: true
      })
    }
    return user!!.wsUrl
  },
  component: RouteComponent,
})

function RouteComponent() {
  const wsURL = Route.useLoaderData();
  return (
    <SocketProvider webSocketUrl={wsURL}>
      <SidebarProvider>
        <AppSidebar />
        <main className='w-full'>
          <Outlet />
        </main>
      </SidebarProvider>
    </SocketProvider>
  );
}

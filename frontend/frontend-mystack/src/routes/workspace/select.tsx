import type { User } from '@/api/auth';
import { workspaceSelectPage } from '@/components/workspace/workspace-select-page'
import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/workspace/select')({
  loader: async ({ context }) => {
    const { queryClient } = context;
    const user = queryClient.getQueryData<User>(["auth", "me"]);
    if (!user || !user.workspaces || user.workspaces.length === 0) {
      redirect({
        to: '/workspace/create',
        replace: true
      })
    }
    return user!!.workspaces
  },
  component: workspaceSelectPage,
})

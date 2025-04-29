import { getUser } from '@/api/auth';
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/workspace')({
  component: RouteComponent,
  beforeLoad: async ({ context }) => {
    const { queryClient } = context;
    const cachedUser = queryClient.getQueryData(["auth", "me"])
    if (cachedUser) {
      return;
    }
    try {
      const user = await queryClient.fetchQuery({
        queryKey: ["auth", "me"],
        queryFn: getUser,
        staleTime: 0
      });

      if (!user) {
        throw new Error('Not Authenticated');
      }
      user.token =  localStorage.getItem("token") || ""

    } catch (e: any) {
      throw redirect({
        to: '/',
        replace: true,
      });
    }
  }
})

function RouteComponent() {
  return <>
    <Outlet />
  </>;
}

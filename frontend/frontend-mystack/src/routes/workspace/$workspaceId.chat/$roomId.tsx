import { useGetRoomConversations } from '@/api/room';
import { ChatScreen } from '@/components/chat';
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/workspace/$workspaceId/chat/$roomId')({
  component: RouteComponent,
})

function RouteComponent() {
  const roomId = Route.useParams().roomId;
  // const workspaceId = Route.useParams().workspaceId;

  const { data, isError, isFetching, error } = useGetRoomConversations(roomId, {
    cursor: undefined,
    limit: 10
  });

  if (!data){
    return <div>Loading...</div>
  }

  if ( isFetching){
    return <div>Loading...</div>
  }

  if (isError){
    return <div>Error: {error.message}</div>
  }

  return <ChatScreen data={data} />
}

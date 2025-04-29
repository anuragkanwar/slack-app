import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/workspace/$workspaceId/chat/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/chat"! index page</div>
}

import { useLocalStorage } from "@/hooks/use-localstorage";
import { useNavigate } from "@tanstack/react-router";
import { Card, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Route } from "@/routes/workspace/select";
import type { WorksapceDtoSmall } from "@/api/workspace";

export const workspaceSelectPage = () => {
  const workspaces = Route.useLoaderData() as WorksapceDtoSmall[]
  
  const [_, setWorkspace] = useLocalStorage<string | null>("workspaceId", null)
  const naviagte = useNavigate();

  const handleOnClick = async (id: string) => {
    setWorkspace(id)
    naviagte({ to: `/workspace/${id}/chat` })
  }

  return (
    <div className="flex h-full flex-col items-center justify-center">
      <div>Select workspace</div>
      {workspaces?.map((x) => {
        return (
          <Card key={x.id}>
            <CardContent>
              <Button onClick={() => handleOnClick(x.id)}>
                {x.name}
              </Button>
            </CardContent>
          </Card>)
      })}
    </div>
  )
}
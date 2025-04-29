import { CornerDownLeft } from "lucide-react"
import { Button } from "../ui/button"
import { ChatInput } from "../ui/chat/chat-input"
import { useState } from "react"
import { usePostChat } from "@/api/chats"
import { useParams } from "@tanstack/react-router"

export const ChatTextArea = () => {
  const [message, setMessage] = useState("")
  const {roomId} = useParams({from : "/workspace/$workspaceId/chat/$roomId"})
  const {mutate} = usePostChat()

  return (
    <div
      className="shrink-0 h-[80px] relative rounded-lg border bg-background flex flex-row gap-2 p-1 items-center"
    >
      <ChatInput
        value={message}
        onChange={(e) => {
          setMessage(e.target.value)
        }}
        placeholder="Type your message here..."
        className="min-h-12 resize-none rounded-lg bg-background border-0 p-3 shadow-none"
      />
      <Button
        size="sm"
        className="ml-auto gap-1.5"
        onClick={() => {
          if(message === ""){
            return
          }
          mutate({message: message, roomId: +roomId}, {
            onSuccess: ()=>{
              setMessage("")
            },
          })
        }}
      >
        Send Message
        <CornerDownLeft className="size-3.5" />
      </Button>
    </div>
  )
}
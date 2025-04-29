import type { ChatHistoryResponseDto } from "@/api/room";
import { ChatMessageList } from "../ui/chat/chat-message-list";
import { ChatBubble, ChatBubbleAvatar, ChatBubbleMessage, ChatBubbleTimestamp } from "../ui/chat/chat-bubble";
import { useUser } from "@/api/auth";
import { ChatTextArea } from "./ChatTextArea";
import { useAutoScroll } from "../ui/chat/hooks/useAutoScroll";

type ChatScreenType = {
  data: ChatHistoryResponseDto
}

export const ChatScreen = ({ data }: ChatScreenType) => {
  const { user } = useUser();
  const { scrollRef } = useAutoScroll();

  return (
    <div className="flex flex-col w-full h-full max-h-full overflow-hidden" >
      <div className="min-h-0 flex-1 overflow-y-auto">
        <ChatMessageList ref={scrollRef}>
          {data.chats.map((ch) => {
            return (
              <ChatBubble variant={ch.user.id === user?.id ? "sent" : "received"}>
                <ChatBubbleAvatar src={ch.user?.avatarUrl} fallback={user?.username.at(0)} />
                <ChatBubbleMessage>
                  {ch.message}
                  <ChatBubbleTimestamp timestamp="12:00" />
                </ChatBubbleMessage>
              </ChatBubble>
            )
          })}
        </ChatMessageList>
      </div>
      <ChatTextArea />
    </div>
  );
}
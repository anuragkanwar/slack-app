// src/contexts/SocketContext.tsx
import React, {
  createContext,
  useContext,
  useEffect,
  useRef,
  useState,
  type ReactNode,
  useCallback,
} from 'react';
import { io, Socket } from 'socket.io-client';
import { useQueryClient } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router'; // Use your router's hook
import { useUser } from '@/api/auth';

import { handleSocketEvent } from "@/services/socketEventHandlers"
import { SocketEvent } from '@/services/SocketEventsEnum';

interface SocketContextProps {
  socket: Socket | null;
  isConnected: boolean;
}

const SocketContext = createContext<SocketContextProps>({
  socket: null,
  isConnected: false,
});

export const useSocket = () => useContext(SocketContext);

interface SocketProviderProps {
  children: ReactNode;
  webSocketUrl: string;
}

export const SocketProvider: React.FC<SocketProviderProps> = ({ children, webSocketUrl }) => {
  const { workspaceId } = useParams({ from: "/workspace/$workspaceId/chat" });
  const { user } = useUser();
  const queryClient = useQueryClient();
  const socketRef = useRef<Socket | null>(null);
  const [isConnected, setIsConnected] = useState(false);

  const currentRouteContext = useRef(workspaceId);
  useEffect(() => {
    currentRouteContext.current = workspaceId;
  }, [workspaceId]);


  const dispatchEvent = useCallback((eventName: SocketEvent, data: any) => {
    console.log(`Dispatching Socket Event: ${eventName}`, data);
    handleSocketEvent(eventName, data, queryClient, currentRouteContext.current);
  }, [queryClient]);


  useEffect(() => {
    if (!workspaceId || !user?.token || !webSocketUrl) {
      if (socketRef.current) {
        console.log('Disconnecting socket due to invalid context...');
        socketRef.current.disconnect();
        socketRef.current = null;
        setIsConnected(false);
      }
      return;
    }

    if (socketRef.current && socketRef.current.connected && socketRef.current.io.opts.query?.workspaceId === workspaceId) {
      console.log(`Socket already connected for workspace ${workspaceId}`);
      return;
    }

    if (socketRef.current && socketRef.current.io.opts.query?.workspaceId !== workspaceId) {
      console.log(`Disconnecting socket from old workspace ${socketRef.current.io.opts.query?.workspaceId}`);
      socketRef.current.disconnect();
      socketRef.current = null;
      setIsConnected(false);
    }


    console.log(`Initializing Socket connection for workspace: ${workspaceId}`);
    const newSocket = io(webSocketUrl, {
      reconnectionAttempts: 2,
      extraHeaders: {
        "X-workspace-id": workspaceId,
        "Authorization": `Bearer ${user?.token}`
      }
    });

    newSocket.on('connect', () => {
      console.log(`Socket connected: ${newSocket.id} for workspace ${workspaceId}`);
      setIsConnected(true);
    });

    newSocket.on('disconnect', (reason) => {
      console.log(`Socket disconnected: ${reason}`);
      setIsConnected(false);
      if (reason === 'io server disconnect') {
      }
    });

    newSocket.on('connect_error', (error) => {
      console.error(`Socket connection error for ${workspaceId}:`, error.message);
      setIsConnected(false);
    });

    newSocket.onAny((eventName: SocketEvent, ...args) => {
      dispatchEvent(eventName, args[0]); // Assuming event data is the first argument
    });

    socketRef.current = newSocket;

    return () => {
      console.log(`Cleaning up Socket connection effect for workspace: ${workspaceId}`);
      if (newSocket) { // Use the socket created in this effect scope
        newSocket.offAny(); // Remove the central listener
        newSocket.disconnect();
        socketRef.current = null; // Clear ref on explicit cleanup
        setIsConnected(false);
      }
    };
  }, [workspaceId, user?.token, webSocketUrl, dispatchEvent]); // Effect Dependencies


  return (
    <SocketContext.Provider value={{ socket: socketRef.current, isConnected }}>
      {children}
    </SocketContext.Provider>
  );
};
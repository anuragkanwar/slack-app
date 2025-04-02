package com.anuragkanwar.slackmessagebackend.socket;

public enum SocketEvent {
    // Server will send these to
    boot_room_list, //On boot, get all rooms list                                   User
    boot_user_list, //On boot, get all users list                                   User

    room_created, //A public room was created,                                      Workspace
    room_deleted, //A public room was deleted,                                      Workspace
    room_joined, //You joined a room,                                               User
    room_left, //You left a room,                                                   User
    room_rename, //A public room was renamed,                                       Workspace

    goodbye, //The server intends to close the connection soon.,  **                User
    hello, //The client has successfully connected to the server,                   User

    manual_presence_change, //You manually updated your presence,                   **SERVER**

    member_joined_room, //A user joined a public room, private room, DM or MUDM.,   Room
    member_left_room, //A user left a public or private room,                       Room

    message, //A message was sent to a room,                                        Room

    presence_change, //A member's presence changed,                                 Workspace, User
//    presence_query, //get current presence status for a list of users,              **SERVER**
//    presence_sub, //Subscribe to presence events for the specified users,           **SERVER**

    typing, // You are typing in a room,                                            **SERER**
    user_typing, //A room member is typing a message,                               Room

    user_joined_workspace, // new user joined workspace,                            Workspace
    user_left_workspace, // user left workspace,                                    Workspace

}












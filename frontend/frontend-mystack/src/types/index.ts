export enum RoomType {
  Public = "Public",
  Private = "Private",
  DM = "DM",
  MUDM = "MUDM",
}

export type UserDtoSmall = {
  id: number
  username: string
  avatarUrl: string
  email :string
}
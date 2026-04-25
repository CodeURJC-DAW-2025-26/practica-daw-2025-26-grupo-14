import { create } from 'zustand'

type UserRole = 'USER' | 'ADMIN'

type AuthUser = {
  id: number
  name: string
  role: UserRole
}

type AuthState = {
  logged: boolean
  id: number | null
  name: string | null
  role: UserRole | null
  login: (user: AuthUser) => void
  logout: () => void
  restoreSession: (user: AuthUser | null) => void
}

export const useAuthStore = create<AuthState>((set) => ({
  logged: false,
  id: null,
  name: null,
  role: null,

  login: (user) =>
    set({
      logged: true,
      id: user.id,
      name: user.name,
      role: user.role,
    }),

  logout: () =>
    set({
      logged: false,
      id: null,
      name: null,
      role: null,
    }),

  restoreSession: (user) =>
    set(
      user
        ? {
            logged: true,
            id: user.id,
            name: user.name,
            role: user.role,
          }
        : {
            logged: false,
            id: null,
            name: null,
            role: null,
          }
    ),
}))

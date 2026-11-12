// TEMPORARY: a former login doesnt exist yet
// with the user from AuthContext, so the shell does not change.
const TEMPORARY_USER = { name: 'Admin', role: 'ADMIN' }

export function useCurrentUser() {
  return { user: TEMPORARY_USER, logout: () => {} }
}
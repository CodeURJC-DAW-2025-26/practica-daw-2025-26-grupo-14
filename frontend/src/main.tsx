import 'bootstrap/dist/css/bootstrap.min.css'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Layout from './Layout'
import HomePage from './HomePage'
import SearchPage from './SearchPage'
import { useAuthStore } from './authStore'
import LoginPage from './LoginPage'

async function initApp() {
  // At startup, check the API if the user is already logged in (e.g. from a previous session)
  try {
    const res = await fetch('/api/v1/auth/me', { credentials: 'include' })
    if (res.ok) {
      const data = await res.json()
      useAuthStore.getState().login({
        id: data.id,
        name: data.name,
        city: data.city ?? null,
        role: data.roles.includes('ADMIN') ? 'ADMIN' : 'USER',
      })
    }
  } catch {
    // ifnNo sesion, it doesnt do nothing
  }

  const router = createBrowserRouter([
    {
      path: '/',
      element: <Layout />,
      children: [
        { index: true, element: <HomePage /> },
        { path: 'search', element: <SearchPage /> },
        { path: 'login', element: <LoginPage /> }
      ],
    },
  ])

  createRoot(document.getElementById('root')!).render(
    <RouterProvider router={router} />
  )
}

initApp()

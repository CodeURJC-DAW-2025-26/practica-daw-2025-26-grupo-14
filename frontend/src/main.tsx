import 'bootstrap/dist/css/bootstrap.min.css'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Layout from './Layout'
import App from './App'

const router = createBrowserRouter(
  [
    {
      path: '/',
      element:   <Layout />,
      children: [
        { index: true, element: <App /> },
      ],
    },
  ],
  {
    basename: '/new',
  }
)

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    
    <RouterProvider router={router} />
  </StrictMode>
)

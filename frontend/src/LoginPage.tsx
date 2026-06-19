import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'

function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const login = useAuthStore((state) => state.login)

async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')

    try {
      // Usamos el endpoint JWT en vez del login de formulario
      const res = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
        credentials: 'include',
        redirect: 'manual', // Not follow redirects automatically
      })

      if (!res.ok && res.status !== 0) {
        setError('Usuario o contraseña incorrectos')
        return
      }

      // Obtenemos los datos del usuario logueado
      const meRes = await fetch('/api/v1/auth/me', { credentials: 'include' })
      if (meRes.ok) {
        const data = await meRes.json()
        login({
          id: data.id,
          name: data.name,
          city: data.city ?? null,
          role: data.roles.includes('ADMIN') ? 'ADMIN' : 'USER',
        })
        navigate('/')
      }
    } catch {
      setError('Error al conectar con el servidor')
    }
  }

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-4">
          <h2 className="mb-4">Log in</h2>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                type="text"
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary w-100">
              Log in
            </button>
          </form>
          <p className="mt-3 text-center">
            Don't have an account? <Link to="/register">Register</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default LoginPage

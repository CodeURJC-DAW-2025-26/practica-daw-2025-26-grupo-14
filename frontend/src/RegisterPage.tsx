

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

function RegisterPage() {
  const [fullName, setFullName] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [city, setCity] = useState('')
  const [dni, setDni] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const navigate = useNavigate()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setSuccess('')

    if (!fullName || !name || !email || !city || !dni || !password || !confirmPassword) {
      setError('Todos los campos son requeridos')
      return
    }

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    try {
      const res = await fetch('/api/v1/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fullName,
          name,
          email,
          city,
          dni,
          password,
        }),
      })

      if (!res.ok) {
        const errorData = await res.json()
        setError(errorData.message || 'Error al registrar usuario')
        return
      }

      setSuccess('Usuario registrado exitosamente')
      setTimeout(() => navigate('/login'), 2000) // Redirigir al login después de 2 segundos
    } catch (err) {
      setError('Error al conectar con el servidor')
    }
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-6 col-md-8">
          <div className="card shadow-sm border-0">
            <div className="card-body p-4">
              <div className="text-center mb-4">
                <h2 className="fw-bold">Create your account</h2>
                <p className="text-muted mb-0">Join our marketplace to buy and sell items</p>
              </div>

              {error && <div className="alert alert-danger">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label">Full Name</label>
                  <input
                    type="text"
                    className="form-control"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Username</label>
                  <input
                    type="text"
                    className="form-control"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">City</label>
                  <select
                    className="form-select"
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    required
                  >
                    <option value="">Select a city</option>
                    <option value="Madrid">Madrid</option>
                    <option value="Barcelona">Barcelona</option>
                    <option value="Valencia">Valencia</option>
                    <option value="Sevilla">Sevilla</option>
                    <option value="Zaragoza">Zaragoza</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label">DNI</label>
                  <input
                    type="text"
                    className="form-control"
                    value={dni}
                    onChange={(e) => setDni(e.target.value)}
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

                <div className="mb-3">
                  <label className="form-label">Confirm Password</label>
                  <input
                    type="password"
                    className="form-control"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                </div>

                <div className="form-check mb-4">
                  <input className="form-check-input" type="checkbox" id="terms" required />
                  <label className="form-check-label" htmlFor="terms">
                    I agree to the Terms & Conditions
                  </label>
                </div>

                <button type="submit" className="btn btn-primary w-100">
                  Create account
                </button>
              </form>

              <p className="mt-3 text-center">
                Already have an account? <a href="/login">Sign in</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage
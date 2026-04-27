

import { useState, useEffect } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'

function RegisterPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEditMode = !!id

  const [fullName, setFullName] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [city, setCity] = useState('')
  const [dni, setDni] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(isEditMode)

  // Load user data in edit mode
  useEffect(() => {
    if (isEditMode && id) {
      fetch(`/api/v1/users/${id}`, { credentials: 'include' })
        .then(res => {
          if (!res.ok) throw new Error('User not found')
          return res.json()
        })
        .then(data => {
          setFullName(data.fullName || '')
          setName(data.name || '')
          setEmail(data.email || '')
          setCity(data.city || '')
          setDni(data.dni || '')
        })
        .catch(err => {
          setError('Error loading user data')
          console.error(err)
        })
        .finally(() => setLoading(false))
    }
  }, [id, isEditMode])

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setSuccess('')

    // In edit mode, password is optional
    if (!isEditMode && (!fullName || !name || !email || !city || !dni || !password || !confirmPassword)) {
      setError('All fields are required')
      return
    }

    if (!isEditMode && password !== confirmPassword) {
      setError('Passwords do not match')
      return
    }

    try {
      const userData: any = {
        fullName,
        name,
        email,
        city,
        dni,
      }

      // Only include password if provided (for edit mode) or required (for register)
      if (password) {
        userData.password = password
      }

      const url = isEditMode ? `/api/v1/users/${id}` : '/api/v1/users'
      const method = isEditMode ? 'PUT' : 'POST'

      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(userData),
      })

      if (!res.ok) {
        const errorData = await res.json()
        setError(errorData.message || `Error ${isEditMode ? 'updating' : 'registering'} user`)
        return
      }

      if (isEditMode) {
        setSuccess('Account updated successfully')
        setTimeout(() => navigate(`/user_account/${id}`), 2000)
      } else {
        setSuccess('User registered successfully')
        setTimeout(() => navigate('/login'), 2000)
      }
    } catch (err) {
      setError('Error connecting to server')
    }
  }

  if (loading) {
    return (
      <div className="container py-5">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-6 col-md-8">
          <div className="card shadow-sm border-0">
            <div className="card-body p-4">
              <div className="text-center mb-4">
                <h2 className="fw-bold">{isEditMode ? 'Edit your account' : 'Create your account'}</h2>
                <p className="text-muted mb-0">
                  {isEditMode ? 'Update your profile information' : 'Join our marketplace to buy and sell items'}
                </p>
              </div>

              {error && <div className="alert alert-danger">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}

              <form onSubmit={handleSubmit}>
                {isEditMode && <input type="hidden" name="id" value={id} />}

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
                  <label className="form-label">
                    {isEditMode ? 'New Password (leave blank to keep current)' : 'Password'}
                  </label>
                  <input
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required={!isEditMode}
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Confirm Password</label>
                  <input
                    type="password"
                    className="form-control"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required={!isEditMode}
                  />
                </div>

                {!isEditMode && (
                  <div className="form-check mb-4">
                    <input className="form-check-input" type="checkbox" id="terms" required />
                    <label className="form-check-label" htmlFor="terms">
                      I agree to the Terms & Conditions
                    </label>
                  </div>
                )}

                <button type="submit" className="btn btn-primary w-100">
                  {isEditMode ? 'Save Changes' : 'Create account'}
                </button>
              </form>

              {!isEditMode && (
                <p className="mt-3 text-center">
                  Already have an account? <Link to="/login">Sign in</Link>
                </p>
              )}
              
              {isEditMode && (
                <p className="mt-3 text-center">
                  <Link to={`/user_account/${id}`}>Back to profile</Link>
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage
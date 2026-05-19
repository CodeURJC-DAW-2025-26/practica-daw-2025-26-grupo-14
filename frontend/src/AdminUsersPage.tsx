import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from './authStore'

type User = {
  id: number
  fullName?: string
  name: string
  email?: string
  roles?: string[]
  isBanned?: boolean
}

function AdminUsersPage() {
  const { logged, role } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [savingId, setSavingId] = useState<number | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!logged) {
      navigate('/login')
      return
    }

    if (role !== 'ADMIN') {
      navigate('/')
      return
    }

    loadUsers()
  }, [logged, role, navigate])
  

  async function loadUsers() {
    setLoading(true)
    setError('')

    try {
      const res = await fetch('/api/v1/users?page=0', {
        credentials: 'include',
      })
      if (!res.ok) {
        throw new Error('Failed to load users')
      }

      const data = await res.json()
      setUsers(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error(err)
      setError('No se pudieron cargar los usuarios.')
    } finally {
      setLoading(false)
    }
  }

  async function deleteUser(userId: number) {
    try {
      await fetch(`/api/v1/users/${userId}`, {
        method: 'DELETE',
        credentials: 'include',
      })
      setUsers((prev) => prev.filter((u) => u.id !== userId))

    } catch (err) {
      console.error(err)
      setError('Failed to delete user.')
    }
  }

  async function toggleBan(user: User) {
    setSavingId(user.id)
    setError('')

    try {
      const res = await fetch(`/api/v1/users/${user.id}`, {
        method: 'PUT',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isBanned: !user.isBanned }),
      })

      if (!res.ok) {
        throw new Error('Failed to update user')
      }

      setUsers((prev) =>
        prev.map((u) =>
          u.id === user.id ? { ...u, isBanned: !user.isBanned } : u
        )
      )
    } catch (err) {
      console.error(err)
      setError('Error actualizando el estado del usuario.')
    } finally {
      setSavingId(null)
    }
  }

  return (
    <div style={{ background: '#f4f6f9', minHeight: '100vh' }}>
      <div className="container-fluid">
        <div className="row">
          <aside className="col-md-2 p-0" style={{ background: '#212529', minHeight: '100vh' }}>
            <h4 className="text-white text-center py-3 border-bottom">Admin Panel</h4>
            <Link
              to="/administrator"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/administrator' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/administrator' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-chart-line me-2"></i>Dashboard
            </Link>
            <Link
              to="/admin_users"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_users' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_users' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-users me-2"></i>Users
            </Link>
            <Link
              to="/admin_listings"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_listings' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_listings' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-flag me-2"></i>Moderation
            </Link>
            <Link
              to="/admin_stats"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_stats' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_stats' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-chart-pie me-2"></i>Statistics
            </Link>
            <Link to="/" className="d-block text-decoration-none py-3 px-3 text-muted" style={{ color: '#adb5bd' }}>
              <i className="fas fa-sign-out-alt me-2"></i>Home
            </Link>
          </aside>

          <main className="col-md-10 p-4">
            <h2 className="mb-4">User Management</h2>
            <div className="card shadow-sm mb-4">
              <div className="card-body">
                <div className="d-flex flex-column flex-md-row justify-content-between gap-3">
                  <div>
                    <p className="mb-0 text-muted">Lista de usuarios registrados y su estado de acceso.</p>
                  </div>
                  <div>
                    <Link to="/administrator" className="btn btn-outline-secondary btn-sm">
                      Back to dashboard
                    </Link>
                  </div>
                </div>
              </div>
            </div>

            <div className="card shadow-sm">
              <div className="card-body">
                <div className="table-responsive">
                  <table className="table table-hover align-middle">
                    <thead>
                      <tr>
                        <th>User</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th className="text-end">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {loading ? (
                        <tr>
                          <td colSpan={5} className="text-center py-5">
                            Loading...
                          </td>
                        </tr>
                      ) : users.length === 0 ? (
                        <tr>
                          <td colSpan={5} className="text-center py-5 text-muted">
                            No users available
                          </td>
                        </tr>
                      ) : (
                        users.map((user) => {
                          const banned = !!user.isBanned
                          const roleLabel = user.roles?.length ? user.roles.join(', ') : 'USER'
                          return (
                            <tr key={user.id}>
                              <td>{user.fullName || user.name}</td>
                              <td>{user.email || '—'}</td>
                              <td>{roleLabel}</td>
                              <td>
                                <span className={`badge bg-${banned ? 'danger' : 'success'}`}>
                                  {banned ? 'Banned' : 'Active'}
                                </span>
                              </td>
                              <td className="text-end">
                                <button
                                  type="button"
                                  className={`btn btn-outline-${banned ? 'success' : 'danger'} btn-sm me-2`}
                                  disabled={savingId === user.id}
                                  onClick={() => toggleBan(user)}
                                >
                                  {savingId === user.id ? 'Saving...' : banned ? 'Unban' : 'Ban'}
                                </button>
                                <Link to={`/user_account/${user.id}`} className="btn btn-outline-secondary btn-sm">
                                  View
                                </Link>
                                <button
                                    type="button"
                                    className="btn btn-outline-dark btn-sm ms-2"
                                    onClick={() => deleteUser(user.id)}
                                    disabled={savingId === user.id}
                                  >
                                    Delete
                                  </button>
                              </td>
                            </tr>
                          )
                        })
                      )}
                    </tbody>
                  </table>
                </div>

                {error && <div className="alert alert-danger mt-3">{error}</div>}
              </div>
            </div>
          </main>
        </div>
      </div>
    </div>
  )
}

export default AdminUsersPage

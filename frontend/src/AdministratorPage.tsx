import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from './authStore'

type AdminStats = {
  totalUsers: number
  totalListings: number
  totalOrders: number
  pendingReports: number
}

const sidebarItem = (path: string, label: string, icon: string, active: boolean) => (
  <Link
    to={path}
    className={`d-block text-decoration-none py-3 px-3 ${active ? 'bg-primary text-white' : 'text-muted'}`}
    style={{ color: active ? '#fff' : '#adb5bd' }}
  >
    <i className={`${icon} me-2`}></i>
    {label}
  </Link>
)

function AdministratorPage() {
  const { logged, role } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [stats, setStats] = useState<AdminStats>({
    totalUsers: 0,
    totalListings: 0,
    totalOrders: 0,
    pendingReports: 0,
  })
  const [loading, setLoading] = useState(true)
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

    async function loadStats() {
      try {
        const [usersRes, productsRes, ordersRes] = await Promise.all([
          fetch('/api/v1/users?page=0', { credentials: 'include' }),
          fetch('/api/v1/products?page=0', { credentials: 'include' }),
          fetch('/api/v1/orders?page=0&size=1', { credentials: 'include' }),
        ])

        if (!usersRes.ok || !productsRes.ok || !ordersRes.ok) {
          throw new Error('Failed to load admin stats')
        }

        const usersData = await usersRes.json()
        const productsData = await productsRes.json()
        const ordersData = await ordersRes.json()

        setStats({
          totalUsers: Array.isArray(usersData) ? usersData.length : 0,
          totalListings: Array.isArray(productsData) ? productsData.length : 0,
          totalOrders: ordersData?.totalElements ?? 0,
          pendingReports: Array.isArray(productsData) ? productsData.filter((product) => product.reported).length : 0,
        })
      } catch (err) {
        console.error(err)
        setError('No se pudieron cargar los datos de administrador.')
      } finally {
        setLoading(false)
      }
    }

    loadStats()
  }, [logged, role, navigate])

  return (
    <div style={{ background: '#f4f6f9', minHeight: '100vh' }}>
      <div className="container-fluid">
        <div className="row">
          <aside className="col-md-2 p-0" style={{ background: '#212529', minHeight: '100vh' }}>
            <h4 className="text-white text-center py-3 border-bottom">Admin Panel</h4>
            {sidebarItem('/administrator', 'Dashboard', 'fas fa-chart-line', location.pathname === '/administrator')}
            {sidebarItem('/admin_users', 'Users', 'fas fa-users', location.pathname === '/admin_users')}
            {sidebarItem('/admin_listings', 'Moderation', 'fas fa-flag', location.pathname === '/admin_listings')}
            {sidebarItem('/admin_stats', 'Statistics', 'fas fa-chart-pie', location.pathname === '/admin_stats')}
            <Link
              to="/"
              className="d-block text-decoration-none py-3 px-3 text-muted"
              style={{ color: '#adb5bd' }}
            >
              <i className="fas fa-sign-out-alt me-2"></i>
              Home
            </Link>
          </aside>

          <main className="col-md-10 p-4">
            <h2 className="mb-4">Dashboard</h2>

            <div className="row g-4">
              <div className="col-md-3">
                <div className="card shadow-sm p-3 h-100" style={{ borderRadius: '12px', border: 'none' }}>
                  <h6 className="text-muted">Total Users</h6>
                  <h3>{loading ? '...' : stats.totalUsers}</h3>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card shadow-sm p-3 h-100" style={{ borderRadius: '12px', border: 'none' }}>
                  <h6 className="text-muted">Active Listings</h6>
                  <h3>{loading ? '...' : stats.totalListings}</h3>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card shadow-sm p-3 h-100" style={{ borderRadius: '12px', border: 'none' }}>
                  <h6 className="text-muted">Items Sold Today</h6>
                  <h3>—</h3>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card shadow-sm p-3 h-100" style={{ borderRadius: '12px', border: 'none' }}>
                  <h6 className="text-muted">Reports Pending</h6>
                  <h3>{loading ? '...' : stats.pendingReports}</h3>
                </div>
              </div>
            </div>

            <div className="mt-5">
              <h4>Quick actions</h4>
              <div className="row g-3 mt-2">
                <div className="col-md-4">
                  <Link to="/admin_users" className="btn btn-outline-primary w-100 p-3">
                    <i className="fas fa-user-slash me-2"></i>Manage Users
                  </Link>
                </div>
                <div className="col-md-4">
                  <Link to="/admin_listings" className="btn btn-outline-danger w-100 p-3">
                    <i className="fas fa-ban me-2"></i>Moderate Listings
                  </Link>
                </div>
                <div className="col-md-4">
                  <Link to="/admin_stats" className="btn btn-outline-success w-100 p-3">
                    <i className="fas fa-chart-bar me-2"></i>View Statistics
                  </Link>
                </div>
              </div>
            </div>

            {error && <div className="alert alert-danger mt-4">{error}</div>}
          </main>
        </div>
      </div>
    </div>
  )
}

export default AdministratorPage

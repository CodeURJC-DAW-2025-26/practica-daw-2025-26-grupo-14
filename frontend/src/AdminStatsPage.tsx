import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from './authStore'

type CategoryStat = {
  category: string
  count: number
  percent: number
}

type AdminStatsData = {
  totalUsers: number
  totalListings: number
  totalRatings: number
  categoryStats: CategoryStat[]
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

function AdminStatsPage() {
  const { logged, role } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [stats, setStats] = useState<AdminStatsData>({
    totalUsers: 0,
    totalListings: 0,
    totalRatings: 0,
    categoryStats: [],
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
        const [usersRes, productsRes] = await Promise.all([
          fetch('/api/v1/users?page=0', { credentials: 'include' }),
          fetch('/api/v1/products?page=0', { credentials: 'include' }),
        ])

        if (!usersRes.ok || !productsRes.ok) {
          throw new Error('Failed to load admin stats')
        }

        const usersData = await usersRes.json()
        const productsData = await productsRes.json()

        // Calculate category statistics
        const categoryMap = new Map<string, number>()
        const products = Array.isArray(productsData) ? productsData : []
        
        products.forEach((product: any) => {
          const category = product.category || 'Unknown'
          categoryMap.set(category, (categoryMap.get(category) || 0) + 1)
        })

        // Convert to array and calculate percentages
        const totalProducts = Array.from(categoryMap.values()).reduce((a, b) => a + b, 0)
        const categoryStats: CategoryStat[] = Array.from(categoryMap.entries())
          .map(([category, count]) => ({
            category,
            count,
            percent: totalProducts > 0 ? Math.round((count / totalProducts) * 100) : 0,
          }))
          .sort((a, b) => b.count - a.count)

        setStats({
          totalUsers: Array.isArray(usersData) ? usersData.length : 0,
          totalListings: products.length,
          totalRatings: 0, // Will be updated with actual ratings count from API if available
          categoryStats,
        })
      } catch (err) {
        console.error(err)
        setError('No se pudieron cargar los datos de estadísticas.')
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
            <h2 className="mb-4">Statistics</h2>

            {error && (
              <div className="alert alert-danger alert-dismissible fade show" role="alert">
                {error}
                <button
                  type="button"
                  className="btn-close"
                  data-bs-dismiss="alert"
                  aria-label="Close"
                ></button>
              </div>
            )}

            {loading ? (
              <div className="text-center">
                <div className="spinner-border" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              </div>
            ) : (
              <>
                {/* Summary Cards */}
                <div className="row mt-4 g-4 mb-5">
                  <div className="col-md-4">
                    <div className="card shadow-sm p-4 h-100">
                      <h6 className="text-muted mb-2">Total Users</h6>
                      <h3 className="mb-0">{stats.totalUsers}</h3>
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="card shadow-sm p-4 h-100">
                      <h6 className="text-muted mb-2">Total Ratings</h6>
                      <h3 className="mb-0">{stats.totalRatings}</h3>
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="card shadow-sm p-4 h-100">
                      <h6 className="text-muted mb-2">Listings Created</h6>
                      <h3 className="mb-0">{stats.totalListings}</h3>
                    </div>
                  </div>
                </div>

                {/* Sales Statistics */}
                <div className="row mt-4 g-4">
                  <h4 className="mb-3">Sales Statistics</h4>
                  <div className="col-12 col-lg-6">
                    <div className="card shadow-sm p-4 h-100">
                      <h5 className="mb-3">Most Popular Categories</h5>
                      {stats.categoryStats.length > 0 ? (
                        stats.categoryStats.map((stat) => (
                          <div key={stat.category} className="mb-3">
                            <p className="mb-2">
                              <strong>{stat.category}</strong> ({stat.percent}%)
                            </p>
                            <div className="progress">
                              <div
                                className="progress-bar bg-primary"
                                role="progressbar"
                                style={{ width: `${stat.percent}%` }}
                                aria-valuenow={stat.percent}
                                aria-valuemin={0}
                                aria-valuemax={100}
                              ></div>
                            </div>
                          </div>
                        ))
                      ) : (
                        <p className="text-muted">No category data available.</p>
                      )}
                    </div>
                  </div>

                  <div className="col-12 col-lg-6">
                    <div className="card shadow-sm p-4 h-100">
                      <h5 className="mb-3">Product Historical</h5>
                      <p className="text-muted">
                        Chart integration available when connected to backend API
                      </p>
                      {/* Placeholder for Chart.js integration */}
                      <canvas id="productsByDateChart" height="180"></canvas>
                    </div>
                  </div>
                </div>

                {/* Users Statistics */}
                <div className="row mt-4 g-4 mb-5">
                  <h4 className="mb-3 mt-4">Users Statistics</h4>
                  <div className="col-12 col-lg-6">
                    <div className="card shadow-sm p-4 h-100">
                      <h5 className="mb-3">User Historical</h5>
                      <p className="text-muted">
                        Chart integration available when connected to backend API
                      </p>
                      {/* Placeholder for Chart.js integration */}
                      <canvas id="usersByDateChart" height="180"></canvas>
                    </div>
                  </div>

                  <div className="col-12 col-lg-6">
                    <div className="card shadow-sm p-4 h-100">
                      <h5 className="mb-3">User Ratings</h5>
                      <p className="text-muted">
                        Chart integration available when connected to backend API
                      </p>
                      {/* Placeholder for Chart.js integration */}
                      <canvas id="usersRatingsChart" height="180"></canvas>
                    </div>
                  </div>
                </div>
              </>
            )}
          </main>
        </div>
      </div>
    </div>
  )
}

export default AdminStatsPage

import { useEffect, useState, useRef } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'
import {
  Chart,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
} from 'chart.js'

Chart.register(
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
)

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
    // -----------------------------
    // REFERENCES TO CHART ELEMENTS
    // -----------------------------
    const productChartRef = useRef<HTMLCanvasElement | null>(null)
    const userChartRef = useRef<HTMLCanvasElement | null>(null)
    const ratingChartRef = useRef<HTMLCanvasElement | null>(null)
 
  // --------------------
  // STATE
  // --------------------
  const [stats, setStats] = useState<AdminStatsData>({
    totalUsers: 0,
    totalListings: 0,
    totalRatings: 0,
    categoryStats: [],
  })
  const [productChartData, setProductChartData] = useState<any>(null)
const [userChartData, setUserChartData] = useState<any>(null)
const [ratingChartData, setRatingChartData] = useState<any>(null)
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
        const [categoriesRes, productsRes, userHistoricalRes, ratingsRes] = await Promise.all([
          fetch('/api/v1/charts/categories', { credentials: 'include' }),
          fetch('/api/v1/charts/productHistorical', { credentials: 'include' }),
          fetch('/api/v1/charts/userHistorical', { credentials: 'include' }),
          fetch('/api/v1/charts/ratings', { credentials: 'include' }),
        ])

        if (!categoriesRes.ok || !productsRes.ok || !userHistoricalRes.ok || !ratingsRes.ok) {
          throw new Error('Failed to load admin stats')
        }

        const categoriesData = await categoriesRes.json()
        const productsData = await productsRes.json()
        const usersData = await userHistoricalRes.json()
        const ratingsData = await ratingsRes.json()

      // -----------------------------
    // TOTALS
    // -----------------------------
    const totalListings = productsData.data?.Total ?? 0
    const totalUsers = usersData.data?.Total ?? 0
    const totalRatings = ratingsData.data?.Total ?? 0

    // -----------------------------
    // CATEGORY STATS
    // -----------------------------
    const categoryStats = Object.entries(categoriesData.data || {})
      .filter(([key]) => key !== 'Total')
      .map(([category, count]) => ({
        category,
        count: Number(count),
        percent:
          totalListings > 0
            ? Math.round((Number(count) / totalListings) * 100)
            : 0,
      }))

    setStats({
      totalUsers,
      totalListings,
      totalRatings,
      categoryStats,
    })

    // -----------------------------
    // (Optional) CHART DATA PREP
    // -----------------------------
    const productLabels = Object.keys(productsData.data || {}).filter(k => k !== 'Total')
    const productValues = productLabels.map(k => productsData.data[k])

    const userLabels = Object.keys(usersData.data || {}).filter(k => k !== 'Total')
    const userValues = userLabels.map(k => usersData.data[k])

    const ratingLabels = Object.keys(ratingsData.data || {}).filter(k => k !== 'Total')
    const ratingValues = ratingLabels.map(k => ratingsData.data[k])

    // Aquí luego puedes meter Chart.js si quieres:
    console.log({ productLabels, productValues })
    console.log({ userLabels, userValues })
    console.log({ ratingLabels, ratingValues })

    setProductChartData({
      labels: productLabels,
      values: productValues,
    })

    setUserChartData({
      labels: userLabels,
      values: userValues,
    })

    setRatingChartData({
      labels: ratingLabels,
      values: ratingValues,
    })

  } catch (err) {
    console.error(err)
    setError('Could not load statistics data.')
  } finally {
    setLoading(false)
  }
    }

    loadStats()
  }, [logged, role, navigate])


  useEffect(() => {
    if (!productChartData || !userChartData || !ratingChartData) return

    // destroy old charts (importante)
    Chart.getChart('productsByDateChart')?.destroy()
    Chart.getChart('usersByDateChart')?.destroy()
    Chart.getChart('usersRatingsChart')?.destroy()

    // PRODUCTS
    if (productChartRef.current) {
      new Chart(productChartRef.current, {
        type: 'bar',
        data: {
          labels: productChartData.labels,
          datasets: [
            {
              label: 'Products',
              data: productChartData.values,
            },
          ],
        },
      })
    }

    // USERS
    if (userChartRef.current) {
      new Chart(userChartRef.current, {
        type: 'bar',
        data: {
          labels: userChartData.labels,
          datasets: [
            {
              label: 'Users',
              data: userChartData.values,
            },
          ],
        },
      })
    }

    // RATINGS
    if (ratingChartRef.current) {
      new Chart(ratingChartRef.current, {
        type: 'bar',
        data: {
          labels: ratingChartData.labels,
          datasets: [
            {
              label: 'Ratings',
              data: ratingChartData.values,
            },
          ],
        },
      })
    }
  }, [productChartData, userChartData, ratingChartData])

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
                      <canvas ref={productChartRef} height="180"></canvas>
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
                      <canvas ref={userChartRef} height="180"></canvas>
                    </div>
                  </div>

                  <div className="col-12 col-lg-6">
                    <div className="card shadow-sm p-4 h-100">
                      <h5 className="mb-3">User Ratings</h5>
                      <p className="text-muted">
                        Chart integration available when connected to backend API
                      </p>
                      {/* Placeholder for Chart.js integration */}
                      <canvas ref={ratingChartRef} height="180"></canvas>
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

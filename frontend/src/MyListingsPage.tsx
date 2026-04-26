import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from './authStore'

type Product = {
  id: number
  name: string
  price: number
  imageIds: number[]
  createdAt: string
}

function MyListingsPage() {
  const { id: userId, logged } = useAuthStore()
  const navigate = useNavigate()
  const [products, setProducts] = useState<Product[]>([])
  const [page, setPage] = useState(0)
  const [hasNext, setHasNext] = useState(true)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!logged) {
      navigate('/login')
      return
    }
    loadMore(0)
  }, [])

  async function loadMore(currentPage: number) {
    if (loading) return
    setLoading(true)

    const res = await fetch(`/api/v1/products?sellerId=${userId}&page=${currentPage}`, {
      credentials: 'include'
    })
    const data = await res.json()
    const items = Array.isArray(data) ? data : data.content ?? []

    setProducts(prev => currentPage === 0 ? items : [...prev, ...items])
    setPage(currentPage + 1)
    setHasNext(items.length >= 10)
    setLoading(false)
  }

  async function handleDelete(productId: number) {
    if (!confirm('Are you sure you want to delete this item?')) return

    await fetch(`/api/v1/products/${productId}`, {
      method: 'DELETE',
      credentials: 'include'
    })

    setProducts(prev => prev.filter(p => p.id !== productId))
  }

  return (
    <>
      {/* Header */}
      <div className="bg-light py-4 border-bottom mb-4">
        <div className="container d-flex justify-content-between align-items-center">
          <div>
            <h2 className="mb-0">My listings</h2>
            <small className="text-muted">Items you are currently selling</small>
          </div>
          <Link to="/publish" className="btn btn-primary">+ New item</Link>
        </div>
      </div>

      {/* Grid */}
      <div className="container mb-5">
        <div className="row g-4">
          {products.map(p => (
            <div className="col-md-4" key={p.id}>
              <div className="card h-100 shadow-sm">
                <Link to={`/product/${p.id}`}>
                  <img
                    src={p.imageIds?.[0] ? `/images/${p.imageIds[0]}` : '/my_images/noImage.png'}
                    className="card-img-top rounded-2"
                    style={{ aspectRatio: '1/1', objectFit: 'cover' }}
                  />
                </Link>
                <div className="card-body">
                  <h5 className="card-title">{p.name}</h5>
                  <p className="text-success fw-bold">{p.price} €</p>
                  <small className="text-muted">Published on {new Date(p.createdAt).toLocaleDateString()}</small>
                </div>
                <div className="card-footer bg-white border-0 d-flex justify-content-between">
                  <Link to={`/editproduct/${p.id}`} className="btn btn-outline-secondary btn-sm">Edit</Link>
                  <button
                    className="btn btn-outline-danger btn-sm"
                    onClick={() => handleDelete(p.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {hasNext && (
          <div className="text-center mt-4">
            <button
              className="btn btn-outline-dark"
              onClick={() => loadMore(page)}
              disabled={loading}
            >
              {loading ? 'Loading...' : 'Load more'}
            </button>
          </div>
        )}

        {products.length === 0 && !loading && (
          <div className="text-center mt-5">
            <p className="text-muted">You have no listings yet.</p>
            <Link to="/publish" className="btn btn-primary">Publish your first item</Link>
          </div>
        )}
      </div>
    </>
  )
}

export default MyListingsPage
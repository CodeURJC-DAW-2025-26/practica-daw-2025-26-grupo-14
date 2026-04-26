import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuthStore } from './authStore'

function RatingFormPage() {
  const { orderId, ratingId } = useParams<{ orderId?: string; ratingId?: string }>()
  const navigate = useNavigate()
  const userId = useAuthStore((state) => state.id)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [existingRating, setExistingRating] = useState<any>(null)
  const [formData, setFormData] = useState({
    rating: 5,
    summery: '',
    description: '',
  })

  const isEdit = !!ratingId

  useEffect(() => {
    if (isEdit && ratingId) {
      // Load existing rating
      fetch(`/api/v1/ratings/${ratingId}`, { credentials: 'include' })
        .then(res => {
          if (!res.ok) throw new Error('Failed to load rating')
          return res.json()
        })
        .then(data => {
          setExistingRating(data)
          setFormData({
            rating: data.rating || 5,
            summery: data.summery || '',
            description: data.description || '',
          })
        })
        .catch(err => {
          console.error(err)
          setError('Failed to load rating')
        })
    }
  }, [isEdit, ratingId])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      let url = '/api/v1/ratings'
      let method = 'POST'
      let body: any = {
        rating: formData.rating,
        summery: formData.summery,
        description: formData.description,
      }

      if (isEdit && ratingId) {
        url = `/api/v1/ratings/${ratingId}`
        method = 'PUT'
        body.raterId = existingRating.raterId
        body.ratedId = existingRating.ratedId
      } else if (orderId) {
        const orderRes = await fetch(`/api/v1/orders/${orderId}`, { credentials: 'include' })
        if (!orderRes.ok) throw new Error('Failed to load order')
        const order = await orderRes.json()
        
        const isSeller = order.product?.sellerId === userId
        body.ratedId = isSeller ? order.buyerId : order.product?.sellerId
        body.raterId = userId
      } else {
        throw new Error('Missing orderId or ratingId')
      }

      if (!body.ratedId || !body.raterId) {
        throw new Error('Missing raterId or ratedId')
      }

      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(body),
      })

      if (!res.ok) {
        throw new Error(`Failed to ${isEdit ? 'update' : 'create'} rating`)
      }

      navigate('/my_deals')
    } catch (err) {
      console.error(err)
      setError(`Failed to ${isEdit ? 'update' : 'create'} rating`)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: name === 'rating' ? Number(value) : value,
    }))
  }

  return (
    <div className="container mt-4">
      <h2>{isEdit ? 'Edit Rating' : 'Create Rating'}</h2>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="rating" className="form-label">Rating (1-5)</label>
          <select
            id="rating"
            name="rating"
            className="form-select"
            value={formData.rating}
            onChange={handleChange}
            required
          >
            <option value={1}>1 - Very Poor</option>
            <option value={2}>2 - Poor</option>
            <option value={3}>3 - Average</option>
            <option value={4}>4 - Good</option>
            <option value={5}>5 - Excellent</option>
          </select>
        </div>

        <div className="mb-3">
          <label htmlFor="summery" className="form-label">Summary</label>
          <input
            type="text"
            id="summery"
            name="summery"
            className="form-control"
            value={formData.summery}
            onChange={handleChange}
            maxLength={100}
          />
        </div>

        <div className="mb-3">
          <label htmlFor="description" className="form-label">Description</label>
          <textarea
            id="description"
            name="description"
            className="form-control"
            rows={4}
            value={formData.description}
            onChange={handleChange}
            maxLength={500}
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving...' : (isEdit ? 'Update Rating' : 'Create Rating')}
        </button>
        <button type="button" className="btn btn-secondary ms-2" onClick={() => navigate('/my_deals')}>
          Cancel
        </button>
      </form>
    </div>
  )
}

export default RatingFormPage
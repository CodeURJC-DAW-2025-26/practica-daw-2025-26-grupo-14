import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

function PublishPage() {
  const navigate = useNavigate()
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    name: '',
    price: '',
    category: 'Electronics',
    condition: 'New',
    shortDescription: '',
    fullDescription: '',
    contactPreference: 'Chat',
  })
  const [images, setImages] = useState<FileList | null>(null)

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')

    const formData = new FormData()
    formData.append('name', form.name)
    formData.append('price', form.price)
    formData.append('category', form.category)
    formData.append('condition', form.condition)
    formData.append('shortDescription', form.shortDescription)
    formData.append('fullDescription', form.fullDescription)
    formData.append('contactPreference', form.contactPreference)

    if (images) {
      Array.from(images).forEach(img => formData.append('imageFields', img))
    }

    const res = await fetch('/api/v1/products', {
      method: 'POST',
      credentials: 'include',
      body: formData,
    })

    if (!res.ok) {
      setError('Error publishing product. Please try again.')
      return
    }

    const data = await res.json()
    navigate(`/product/${data.id}`)
  }

  return (
    <>
      <div className="bg-primary text-white py-4 mb-4">
        <div className="container">
          <h2 className="mb-0">Post a new item</h2>
          <small>Create a listing to sell your product</small>
        </div>
      </div>

      <div className="container mb-5">
        <div className="card shadow-sm">
          <div className="card-body p-4">
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="row g-4">

                {/* Name */}
                <div className="col-12">
                  <label className="form-label">Item name</label>
                  <input
                    type="text"
                    className="form-control"
                    name="name"
                    placeholder="e.g. iPhone 12 128GB Black"
                    value={form.name}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Price */}
                <div className="col-md-4">
                  <label className="form-label">Price (€)</label>
                  <input
                    type="number"
                    className="form-control"
                    name="price"
                    placeholder="150"
                    value={form.price}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Category */}
                <div className="col-md-4">
                  <label className="form-label">Category</label>
                  <select className="form-select" name="category" value={form.category} onChange={handleChange}>
                    <option>Electronics</option>
                    <option>Clothing</option>
                    <option>Home</option>
                    <option>Sports</option>
                    <option>Books</option>
                    <option>Other</option>
                  </select>
                </div>

                {/* Condition */}
                <div className="col-md-4">
                  <label className="form-label">Condition</label>
                  <select className="form-select" name="condition" value={form.condition} onChange={handleChange}>
                    <option>New</option>
                    <option>Like new</option>
                    <option>Good</option>
                    <option>Acceptable</option>
                    <option>For parts</option>
                  </select>
                </div>

                {/* Short description */}
                <div className="col-12">
                  <label className="form-label">Short description</label>
                  <textarea
                    className="form-control"
                    rows={3}
                    name="shortDescription"
                    placeholder="Brief description of your item..."
                    value={form.shortDescription}
                    onChange={handleChange}
                  />
                </div>

                {/* Full description */}
                <div className="col-12">
                  <label className="form-label">Description</label>
                  <textarea
                    className="form-control"
                    rows={5}
                    name="fullDescription"
                    placeholder="Describe your item, condition, pickup or shipping..."
                    value={form.fullDescription}
                    onChange={handleChange}
                  />
                </div>

                {/* Images */}
                <div className="col-12">
                  <label className="form-label">Images</label>
                  <input
                    type="file"
                    className="form-control"
                    accept=".jpg,.jpeg,.webp"
                    multiple
                    onChange={e => setImages(e.target.files)}
                  />
                </div>

                {/* Contact preference */}
                <div className="col-md-6">
                  <label className="form-label">Contact preference</label>
                  <select className="form-select" name="contactPreference" value={form.contactPreference} onChange={handleChange}>
                    <option>Chat</option>
                    <option>Phone</option>
                    <option>Both</option>
                  </select>
                </div>

                {/* Submit */}
                <div className="col-12 text-end">
                  <button type="submit" className="btn btn-primary btn-lg">Publish item</button>
                </div>

              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  )
}

export default PublishPage
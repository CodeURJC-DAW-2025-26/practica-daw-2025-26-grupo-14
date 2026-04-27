import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

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

  const [existingImages, setExistingImages] = useState<any[]>([])
  const [imagesToDelete, setImagesToDelete] = useState<number[]>([])

  const { id } = useParams()
  const isEditMode = !!id

  useEffect(() => {
    if (isEditMode) {
      fetch(`/api/v1/products/${id}`, { credentials: 'include' })
      .then (res => res.json())
      .then (data => {
        setForm({
          name: data.name,
          price: data.price.toString(),
          category: data.category,
          condition: data.condition,
          shortDescription: data.shortDescription,
          fullDescription: data.fullDescription,
          contactPreference: data.contactPreference,
        })

        setExistingImages(data.images ?? [])
        console.log("IMAGES FROM BACKEND:", data.images)
      })
    }
  }, [id])


  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e: React.FormEvent) {
      e.preventDefault()
      setError('')



      // crate or update product

      const url = isEditMode ? `/api/v1/products/${id}` : '/api/v1/products'

      const method = isEditMode ? 'PUT' : 'POST'

      const res = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          name: form.name,
          price: parseFloat(form.price),
          category: form.category,
          condition: form.condition,
          shortDescription: form.shortDescription,
          fullDescription: form.fullDescription,
          contactPreference: form.contactPreference,
        }),
      })

      if (!res.ok) {
        setError('Error publishing product. Please try again.')
        return
      }

      let productId: number

      if (isEditMode) {
        productId = Number(id)
      } else {
        const data = await res.json()
        productId = data.id
      }

      // upload images if any

      // DELETE selected images (ONLY EDIT)
      if (isEditMode && imagesToDelete.length > 0) {
        await fetch(`/api/v1/products/${productId}/images`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(imagesToDelete),
        })
      }
      if (images && images.length > 0) {
        const formData = new FormData()
        Array.from(images).forEach(img => formData.append('imageFiles', img))

        await fetch(`/api/v1/products/${productId}/images`, {
          method: 'POST',
          credentials: 'include',
          body: formData,
        })
      }

      navigate('/my_listings')
  }

  return (
    <>
      <div className="bg-primary text-white py-4 mb-4">
        <div className="container">
          <h2>{isEditMode ? 'Edit item' : 'Post a new item'}</h2>
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

                {/* Existing images (ONLY EDIT MODE) */}
                {isEditMode && existingImages.length > 0 && (
                  <div className="col-12">
                    <label className="form-label">Current images:</label>
                    <div className="d-flex gap-2 flex-wrap">
                      {existingImages.map(img => (
                        <div key={img.id}>
                          <img
                            src={`/images/${img.id}`}
                            width="100"
                            height="100"
                            style={{ objectFit: 'cover' }}
                          />
                          <div>
                            <label>
                              <input
                                type="checkbox"
                                onChange={(e) => {
                                  if (e.target.checked) {
                                    setImagesToDelete(prev => [...prev, img.id])
                                  } else {
                                    setImagesToDelete(prev => prev.filter(i => i !== img.id))
                                  }
                                }}
                              />
                              Delete
                            </label>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

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
                  <button type="submit">{isEditMode ? 'Update item' : 'Publish item'}</button>
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
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useAuthStore } from './authStore'

type Seller = {
  id: number
  name: string
  profilePictureId?: number
  avgRating?: number
  avgRatingPercent?: number
}

type Product = {
  id: number
  name: string
  price: number
  shortDescription: string
  fullDescription: string
  category: string
  condition: string
  imageIds: number[]
  sellerId: number
  seller?: Seller
}

function ProductPage() {
  const { id } = useParams()
  const { logged, id: myId } = useAuthStore()
  const [product, setProduct] = useState<Product | null>(null)
  const [mainImage, setMainImage] = useState<string>('')
  const [loading, setLoading] = useState(true)
  const [reportVisible, setReportVisible] = useState(false)
  const [reportMessage, setReportMessage] = useState('')
  const [sellerProducts, setSellerProducts] = useState<Product[]>([])
  const [sellerPage, setSellerPage] = useState(0)
  const [sellerHasNext, setSellerHasNext] = useState(true)
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([])
  const [relatedPage, setRelatedPage] = useState(0)
  const [relatedHasNext, setRelatedHasNext] = useState(true)

  useEffect(() => {
    fetch(`/api/v1/products/${id}`, { credentials: 'include' })
      .then(res => res.json())
      .then(data => {
        setProduct(data)
        setMainImage(data.imageIds?.[0] ? `/images/${data.imageIds[0]}` : '/my_images/noImage.png')
        setLoading(false)
      })
  }, [id])

  useEffect(() => {
    if (product) {
      loadSellerProducts(0)
      loadRelatedProducts(0)
    }
  }, [product])

  async function loadSellerProducts(page: number) {
    if (!product) return
    const res = await fetch(`/api/v1/products?sellerId=${product.sellerId}&page=${page}`, { credentials: 'include' })
    const data = await res.json()
    const items = Array.isArray(data) ? data : data.content ?? []
    setSellerProducts(prev => page === 0 ? items : [...prev, ...items])
    setSellerPage(page + 1)
    setSellerHasNext(items.length >= 10)
  }

  async function loadRelatedProducts(page: number) {
    if (!product) return
    const res = await fetch(`/api/v1/products?category=${product.category}&page=${page}`, { credentials: 'include' })
    const data = await res.json()
    const items = Array.isArray(data) ? data : data.content ?? []
    setRelatedProducts(prev => page === 0 ? items : [...prev, ...items])
    setRelatedPage(page + 1)
    setRelatedHasNext(items.length >= 10)
  }

  async function handleBuy() {
    await fetch(`/api/v1/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ productId: product?.id })
    })
    alert('Purchase successful!')
  }

  async function handleReport() {
    await fetch(`/api/v1/products/${id}/report`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ message: reportMessage })
    })
    setReportVisible(false)
    alert('Product reported')
  }

  if (loading) return <div className="container mt-5">Loading...</div>
  if (!product) return <div className="container mt-5">Product not found</div>

  const isOwner = myId === product.sellerId

  function ProductCard({ p }: { p: Product }) {
    return (
      <div className="col mb-5">
        <div className="card h-100">
          <img
            src={p.imageIds?.[0] ? `/images/${p.imageIds[0]}` : '/my_images/noImage.png'}
            className="card-img-top rounded-2"
            style={{ aspectRatio: '1/1', objectFit: 'cover' }}
          />
          <div className="card-body p-4 text-center">
            <h5 className="fw-bolder">{p.name}</h5>
            <span className="text-muted">{p.price} €</span>
          </div>
          <div className="card-footer p-4 pt-0 border-top-0 bg-transparent text-center">
            <a className="btn btn-outline-dark mt-auto" href={`/product/${p.id}`}>View</a>
          </div>
        </div>
      </div>
    )
  }

  return (
    <>
      {/* Product section */}
      <section className="py-5">
        <div className="container px-4 px-lg-5 my-5">
          <div className="row gx-4 gx-lg-5 align-items-center">

            {/* Image gallery */}
            <div className="col-md-6">
              <img
                src={mainImage}
                className="img-fluid rounded mb-3"
                style={{ width: '100%', aspectRatio: '1/1', objectFit: 'cover' }}
              />
              <div className="d-flex gap-2 flex-wrap">
                {product.imageIds?.map(imgId => (
                  <img
                    key={imgId}
                    src={`/images/${imgId}`}
                    style={{ width: '70px', height: '70px', objectFit: 'cover', cursor: 'pointer', border: '1px solid #ccc', borderRadius: '4px' }}
                    onClick={() => setMainImage(`/images/${imgId}`)}
                  />
                ))}
              </div>

              {/* Report form */}
              {reportVisible && (
                <div className="mt-3">
                  <label className="form-label">Report reason:</label>
                  <input
                    type="text"
                    className="form-control mb-2"
                    value={reportMessage}
                    onChange={e => setReportMessage(e.target.value)}
                  />
                  <button className="btn btn-primary" onClick={handleReport}>Send report</button>
                </div>
              )}
            </div>

            {/* Product info */}
            <div className="col-md-6">
              <h1 className="display-5 fw-bolder">{product.name}</h1>
              <div className="fs-5 mb-5">
                <span>{product.price} €</span>
              </div>
              <p className="lead">{product.shortDescription}</p>

              {!isOwner && logged && (
                <div className="d-flex gap-3 mb-3">
                  <button className="btn btn-dark flex-shrink-0" onClick={handleBuy}>
                    <i className="bi-lightning-fill me-1"></i> Buy now
                  </button>
                  <button className="btn btn-outline-dark flex-shrink-0" onClick={() => setReportVisible(!reportVisible)}>
                    <i className="bi-cash-coin me-1"></i> Report
                  </button>
                </div>
              )}

              {/* Seller box */}
              {product.seller && (
                <a href={`/user_account/${product.seller.id}`} className="text-decoration-none text-dark">
                  <div className="p-3 border rounded mt-3">
                    <div className="d-flex align-items-center">
                      <img
                        className="rounded-circle me-3"
                        src={product.seller.profilePictureId ? `/images/${product.seller.profilePictureId}` : '/my_images/noImage.png'}
                        width="70" height="70"
                        style={{ objectFit: 'cover' }}
                      />
                      <div>
                        <h5 className="mb-1">{product.seller.name}</h5>
                        <div style={{ position: 'relative', display: 'inline-block', fontSize: '1.2rem', color: '#ccc' }}>
                          <span>★★★★★</span>
                          <span style={{
                            position: 'absolute', top: 0, left: 0, overflow: 'hidden', color: '#f5a623',
                            width: `${product.seller.avgRatingPercent ?? 0}%`
                          }}>★★★★★</span>
                        </div>
                        <span className="ms-2">{product.seller.avgRating ?? 0}</span>
                        <br />
                        <small className="text-muted">Member since 2021</small>
                      </div>
                    </div>
                  </div>
                </a>
              )}
            </div>

            {/* Full description */}
            <p className="lead mt-4">{product.fullDescription}</p>
          </div>
        </div>
      </section>

      {/* Seller products */}
      <section className="py-5 bg-light">
        <div className="container px-4 px-lg-5 mt-5">
          <h2 className="fw-bolder mb-4">Other products from this seller</h2>
          <div className="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4">
            {sellerProducts.filter(p => p.id !== product.id).map(p => (
              <ProductCard key={p.id} p={p} />
            ))}
          </div>
          {sellerHasNext && (
            <div className="text-center mt-3">
              <button className="btn btn-outline-dark" onClick={() => loadSellerProducts(sellerPage)}>Load more</button>
            </div>
          )}
        </div>
      </section>

      {/* Related products */}
      <section className="py-5 bg-light">
        <div className="container px-4 px-lg-5 mt-5">
          <h2 className="fw-bolder mb-4">Related products</h2>
          <div className="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4">
            {relatedProducts.filter(p => p.id !== product.id).map(p => (
              <ProductCard key={p.id} p={p} />
            ))}
          </div>
          {relatedHasNext && (
            <div className="text-center mt-3">
              <button className="btn btn-outline-dark" onClick={() => loadRelatedProducts(relatedPage)}>Load more</button>
            </div>
          )}
        </div>
      </section>
    </>
  )
}

export default ProductPage
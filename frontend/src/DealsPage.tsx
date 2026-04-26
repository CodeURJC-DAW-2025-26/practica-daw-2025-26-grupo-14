import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuthStore } from './authStore'

type Order = {
  id: number
  productId: number
  buyerId: number
  ratingId: number | null
  state: string
}

type Product = {
  id: number
  sellerId: number
  name: string
  price: number
  imageIds?: number[]
}

type User = {
  id: number
  name: string
}

type OrderWithProduct = Order & {
  product?: Product
  counterpart?: User
  isSeller: boolean
}

type Rating = {
  id: number
  score: number
  comment: string
}

function getStateBadgeClass(state: string) {
  switch (state.toLowerCase()) {
    case 'accepted':
      return 'bg-success'
    case 'rejected':
    case 'cancelled':
      return 'bg-danger'
    case 'offer sent':
    case 'offer_sent':
      return 'bg-warning text-dark'
    default:
      return 'bg-secondary'
  }
}



function DealsPage() {
  const userId = useAuthStore((state) => state.id)
  const logged = useAuthStore((state) => state.logged)
  const [orders, setOrders] = useState<OrderWithProduct[]>([])
  const [ratings, setRating] = useState<Rating[] | null>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function deleteOrder(orderId: number) {
  try {
    const res = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`, {
      method: 'DELETE',
      credentials: 'include',
    })

    if (!res.ok) {
      throw new Error('Failed to delete order')
    }

    // actualizar estado sin recargar
    setOrders((prev) => prev.filter((o) => o.id !== orderId))
  } catch (err) {
    console.error(err)
    setError('Error deleting deal')
  }
}

async function deleteRating(ratingId: number) {
  try {
    const res = await fetch(`http://localhost:8080/api/v1/ratings/${ratingId}`, {
      method: 'DELETE',
      credentials: 'include',
    })

    if (!res.ok) {
      throw new Error('Failed to delete rating')
    }

    // quitar rating del estado
    setOrders((prev) =>
      prev.map((o) =>
        o.ratingId === ratingId ? { ...o, ratingId: null } : o
      )
    )
  } catch (err) {
    console.error(err)
    setError('Error deleting rating')
  }
}

  useEffect(() => {
    if (!logged || userId === null) {
      setLoading(false)
      return
    }

    async function loadDeals() {
      try {
        const res = await fetch('/api/v1/orders', {
          credentials: 'include',
        })

        if (!res.ok) {
          throw new Error(`Failed to load orders: ${res.status}`)
        }

        const data = await res.json()
        const fetchedOrders: Order[] = data.content ?? []
        const productIds = Array.from(new Set(fetchedOrders.map((order) => order.productId)))

        const products = await Promise.all(
          productIds.map(async (productId) => {
            const productRes = await fetch(`/api/v1/products/${productId}`, {
              credentials: 'include',
            })
            if (!productRes.ok) {
              return null
            }
            return (await productRes.json()) as Product
          })
        )

        const ratings = await Promise.all(
          fetchedOrders.map(async (order) => {
            if (!order.ratingId) return null
            const ratingRes = await fetch(`/api/v1/ratings/${order.ratingId}`, {
              credentials: 'include',
            })
              if (!ratingRes.ok) {
                return null
              }
              return (await ratingRes.json()) as Rating
            })
          )

        setRating(ratings.filter((r): r is Rating => r !== null))

        const productMap = new Map<number, Product>()
        products.forEach((product) => {
          if (product) {
            productMap.set(product.id, product)
          }
        })

        const counterpartIds = Array.from(
          new Set(
            fetchedOrders.flatMap((order) => {
              const product = productMap.get(order.productId)
              if (product?.sellerId === userId) {
                return [order.buyerId]
              }
              return product?.sellerId ? [product.sellerId] : []
            })
          )
        )

        const users = await Promise.all(
          counterpartIds.map(async (userIdToFetch) => {
            const userRes = await fetch(`/api/v1/users/${userIdToFetch}`, {
              credentials: 'include',
            })
            if (!userRes.ok) {
              return null
            }
            return (await userRes.json()) as User
          })
        )

        const userMap = new Map<number, User>()
        users.forEach((user) => {
          if (user) {
            userMap.set(user.id, user)
          }
        })

        const filteredOrders = fetchedOrders
          .map((order) => {
            const product = productMap.get(order.productId)
            const isSeller = product?.sellerId === userId
            const counterpartId = isSeller ? order.buyerId : product?.sellerId
            return {
              ...order,
              product,
              counterpart:
                counterpartId && userMap.has(counterpartId)
                  ? userMap.get(counterpartId)
                  : { id: counterpartId ?? 0, name: `User ${counterpartId ?? order.buyerId}` },
              isSeller,
            }
          })
          .filter((order) => order.buyerId === userId || order.isSeller)

        setOrders(filteredOrders)
      } catch (err) {
        setError('Unable to load your deals. Please try again later.')
      } finally {
        setLoading(false)
      }
    }

    loadDeals()
  }, [logged, userId])

  if (!logged) {
    return (
      <section className="container py-5">
        <div className="alert alert-warning">Please log in to see your deals.</div>
      </section>
    )
  }

  const sellerOrders = orders.filter((order) => order.isSeller)
  const buyerOrders = orders.filter((order) => !order.isSeller)

  return (
    <section className="container py-5">
      <div className="mb-4">
        <h1 className="h3">My Deals</h1>
        <p className="text-muted">Items you are buying, reserved or with active offers.</p>
      </div>

      {loading && <div className="alert alert-info">Loading deals...</div>}
      {error && <div className="alert alert-danger">{error}</div>}
      {!loading && orders.length === 0 && !error && (
        <div className="alert alert-secondary">You don't have any deals yet.</div>
      )}

      {sellerOrders.length > 0 && (
        <>
          <div className="mb-3">
            <h2 className="h5">Deals as Seller</h2>
          </div>
          <div className="row gy-4">
            {sellerOrders.map((order) => {
              const product = order.product
              
              const badgeClass = getStateBadgeClass(order.state)

              return (
                <div className="col-12" key={order.id}>
                  <div className="deal-card">
                    <div className="row gy-3 align-items-center">
                      <div className="col-lg-7">
                        <div className="d-flex">
                          <img
                            src={`/images/${order.product?.imageIds?.[0] ?? 'noImage.png' }`}
                            className="border rounded me-3"
                            style={{ width: 120, height: 120, objectFit: 'cover' }}
                            alt={product?.name ?? 'Product image'}
                          />
                          <div>
                            <Link to={`/product/${product?.id ?? order.productId}`} className="nav-link fw-bold">
                              {product?.name ?? `Product #${order.productId}`}
                            </Link>

                            <p className="text-muted mb-1">Buyer: {order.counterpart?.name}</p>

                            <span className={`badge ${badgeClass}`}>{order.state}</span>

                            <p className="mt-2 mb-0">
                              <strong>Price:</strong> {product?.price ?? 'N/A'}€
                            </p>

                            {order.ratingId ? ( <>
                              <p className="mb-0">
                                <strong>Rating:</strong>{ratings?.find(r => r.id === order.ratingId)?.score ?? 'N/A'} / 5 - {ratings?.find(r => r.id === order.ratingId)?.comment ?? ''}
                              </p>
                              </>
                            ) : (
                              <p className="mb-0">
                                <strong>Rating:</strong> Not yet rated. {' '}
                              </p>
                            )}
                          </div>
                        </div>
                      </div>

                      <div className="col-lg-5 text-lg-end">
                        <Link to={`/product/${product?.id ?? order.productId}`} className="btn btn-outline-primary me-2">
                          View product
                        </Link>
                        <Link to={`/user_account/${order.counterpart?.id}`} className="btn btn-success me-2">
                          See Buyer
                        </Link>
                        <a href={`/chat/${order.id}`} className="btn btn-success me-2">
                          Chat with buyer
                        </a>

                        {order.ratingId ? (
                            <button
                            onClick={() => deleteRating(order.ratingId!)}
                            className="btn btn-outline-primary me-2"
                            >
                            Delete rating
                            </button>
                        ) : null}

                        <button
                        onClick={() => deleteOrder(order.id)}
                        className="btn btn-outline-danger"
                        >
                        Cancel deal
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )}

      {buyerOrders.length > 0 && (
        <>
          <div className="mb-3 mt-5">
            <h2 className="h5">Deals as Buyer</h2>
          </div>
          <div className="row gy-4">
            {buyerOrders.map((order) => {
              const product = order.product
            
              const badgeClass = getStateBadgeClass(order.state)

              return (
                <div className="col-12" key={order.id}>
                  <div className="deal-card">
                    <div className="row gy-3 align-items-center">
                      <div className="col-lg-7">
                        <div className="d-flex">
                          <img
                            src={`/images/${order.product?.imageIds?.[0] ?? 'noImage.png' }`}
                            className="border rounded me-3"
                            style={{ width: 120, height: 120, objectFit: 'cover' }}
                            alt={product?.name ?? 'Product image'}
                          />
                          <div>
                            <Link to={`/product/${product?.id ?? order.productId}`} className="nav-link fw-bold">
                              {product?.name ?? `Product #${order.productId}`}
                            </Link>

                            <p className="text-muted mb-1">Seller: {order.counterpart?.name}</p>

                            <span className={`badge ${badgeClass}`}>{order.state}</span>

                            <p className="mt-2 mb-0">
                              <strong>Price:</strong> {product?.price ?? 'N/A'}€
                            </p>

                            {order.ratingId ? ( <>
                              <p className="mb-0">
                                <strong>Rating:</strong>{ratings?.find(r => r.id === order.ratingId)?.score ?? 'N/A'} / 5 - {ratings?.find(r => r.id === order.ratingId)?.comment ?? ''}
                              </p>
                              <p>
                                <Link to={`/edit_rating/${order.ratingId}`} className="link-secondary">
                                  Edit rating
                                </Link>
                              </p> </>
                            ) : (
                              <p className="mb-0">
                                <strong>Rating:</strong>{' '}
                                <Link to={`/create_rating/${order.id}`} className="link-secondary">
                                  Create rating
                                </Link>
                              </p>
                            )}
                          </div>
                        </div>
                      </div>

                      <div className="col-lg-5 text-lg-end">
                        <Link to={`/product/${product?.id ?? order.productId}`} className="btn btn-outline-primary me-2">
                          View product
                        </Link>
                        <Link to={`/user_account/${order.counterpart?.id}`} className="btn btn-success me-2">
                          See Seller
                        </Link>
                        <a href={`/chat/${order.id}`} className="btn btn-success me-2">
                          Chat with seller
                        </a>

                        {order.ratingId ? (
                          <button
                            onClick={() => deleteRating(order.ratingId!)}
                            className="btn btn-outline-primary me-2"
                            >
                            Delete rating
                            </button>
                        ) : null}

                        <button
                          onClick={() => deleteOrder(order.id)}
                          className="btn btn-outline-danger"
                        >
                          Cancel deal
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </>
      )}
    </section>
  )
}

export default DealsPage
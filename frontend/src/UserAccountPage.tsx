import { useEffect, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { useAuthStore } from "./stores/authStore"
import noImage from './assets/noImage.png'

function UserAccountPage() {
  const { id } = useParams()
  const authUser = useAuthStore()
  
  const [user, setUser] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [products, setProducts] = useState<any[]>([])
  const [message, setMessage] = useState("")
  const [isOwner, setIsOwner] = useState(false)
  const [avgRating, setAvgRating] = useState(0)
  const [totalRatings, setTotalRatings] = useState(0)
  const [showProfilePicForm, setShowProfilePicForm] = useState(false)

  useEffect(() => {
    if (!id) return

    Promise.all([
      fetch(`/api/v1/users/${id}`, { credentials: "include" }).then(res => res.json()),
      fetch(`/api/v1/products?sellerId=${id}`).then(res => res.json()),
      fetch(`/api/v1/users/${id}/ratings`).then(res => res.json()).catch(() => ({ avgRating: 0, totalRatings: 0 }))
    ])
      .then(([userData, productsData, ratingsData]) => {
        setUser(userData)
        setProducts(productsData || [])
        setAvgRating(ratingsData.avgRating || 0)
        setTotalRatings(ratingsData.totalRatings || 0)
        
        // Check if current user is the owner
        const currentUserId = authUser.id
        setIsOwner(currentUserId === parseInt(id))
      })
      .catch(err => console.error("Error loading user data:", err))
      .finally(() => setLoading(false))
  }, [id, authUser.id])

  const handleSendMessage = () => {
    // Navigate to deals page or show message option
    alert("Messaging feature - navigate to chat")
  }

  const handleProfilePicChange = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const fileInput = formData.get("imageField") as File
    
    if (!fileInput || fileInput.size === 0) {
      alert("Please select a file")
      return
    }

    try {
      const res = await fetch(`/api/v1/users/${id}/profilePicture`, {
        method: "POST",
        credentials: "include",
        body: formData
      })
      
      if (res.ok) {
        setMessage("Profile picture updated successfully")
        setShowProfilePicForm(false)
        // Reload user data
        const userData = await fetch(`/api/v1/users/${id}`).then(res => res.json())
        setUser(userData)
      } else {
        setMessage("Error updating profile picture")
      }
    } catch (err) {
      setMessage("Error uploading file")
    }
  }

  if (loading) return <div>Loading...</div>
  if (!user) return <div>User not found</div>

 

  return (
    <>
      {/* HEADER */}
      <div className="bg-primary text-white py-4 mb-4">
        <div className="container">
          <h2>User account</h2>
        </div>
      </div>

      {/* USER CARD */}
      <section className="py-5" style={{ backgroundColor: "#f8f9fa" }}>
        <div className="container">
          <div className="card shadow-sm border-0">
            <div className="card-body p-4">

              {message && (
                <div className="alert alert-success">{message}</div>
              )}

              <div className="row align-items-center">

                {/* PROFILE IMAGE */}
                <div className="col-md-3 text-center">
                  <img
                    src={
                      user.profilePicture
                        ? `/images/${user.profilePicture.id}`
                        : noImage
                    }
                    className="rounded-circle img-fluid mb-3"
                    style={{ width: 150, height: 150, objectFit: "cover" }}
                  />

                  {isOwner && (
                    <button 
                      className="btn btn-outline-primary w-100 mt-2"
                      onClick={() => setShowProfilePicForm(!showProfilePicForm)}
                    >
                      Change profile picture
                    </button>
                  )}
                  
                  {showProfilePicForm && isOwner && (
                    <form method="post" encType="multipart/form-data" onSubmit={handleProfilePicChange} className="mt-2">
                      <input type="file" name="imageField" className="form-control" accept=".jpg, .jpeg, .webp" />
                      <button type="submit" className="btn btn-primary mt-2">Save</button>
                    </form>
                  )}
                </div>

                {/* INFO */}
                <div className="col-md-6">
                  <h2>{user.fullName || user.name}</h2>
                  
                  {/* Rating stars */}
                  {totalRatings > 0 && (
                    <div className="mb-2">
                      <span style={{ color: "#ffc107", fontSize: "1.2rem" }}>
                        {"★".repeat(Math.round(avgRating))}
                        {"☆".repeat(5 - Math.round(avgRating))}
                      </span>
                      <span className="ms-2 text-muted">{avgRating.toFixed(1)} ({totalRatings} reviews)</span>
                    </div>
                  )}

                  <p className="text-muted mb-1">
                    📍 {user.city}
                  </p>

                  <p className="text-muted mb-1">
                    DNI: {user.dni}
                  </p>

                  <p className="text-muted mb-1">
                    Member since {user.createdAt}
                  </p>

                  <p className="text-muted mb-0">
                    📧 {user.email}
                  </p>
                </div>

                {/* ACTIONS */}
                <div className="col-md-3 text-center">
                  {isOwner ? (
                    <>
                      <Link
                        to={`/edituser/${user.id}`}
                        className="btn btn-primary w-100 mb-2"
                      >
                        Edit
                      </Link>
                    </>
                  ) : (
                    <>
                      <button className="btn btn-primary w-100 mb-2">
                        Follow
                      </button>
                      <button className="btn btn-outline-primary w-100" onClick={handleSendMessage}>
                        Send message
                      </button>
                    </>
                  )}
                </div>

              </div>
            </div>
          </div>
        </div>
      </section>

      {/* PRODUCTS */}
      <section className="py-5">
        <div className="container">
          <h3 className="mb-4">Items for sale</h3>

          <div className="row">
            {products.length > 0 ? (
              products.map(p => (
                <div key={p.id} className="col-lg-3 col-md-6 mb-4">
                  <div className="card h-100 shadow-sm">

                    <img
                      src={
                        p.images && p.images.length > 0
                          ? `/images/${p.images[0].id}`
                          : noImage
                      }
                      className="card-img-top"
                      style={{ height: 200, objectFit: "cover" }}
                    />

                    <div className="card-body">
                      <h5>{p.name}</h5>
                      <p className="text-success fw-bold">€{p.price}</p>
                      <p className="text-muted small">
                        {p.shortDescription}
                      </p>

                      <Link
                        to={`/product/${p.id}`}
                        className="btn btn-outline-dark w-100"
                      >
                        View item
                      </Link>
                    </div>

                  </div>
                </div>
              ))
            ) : (
              <p>This user has no products yet.</p>
            )}
          </div>
        </div>
      </section>
    </>
  )
}

export default UserAccountPage
import { useAuthStore } from "./stores/authStore"
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import "./styles.css"

function Header() {
    const logged = useAuthStore((state) => state.logged)
    const role = useAuthStore((state) => state.role)
    const logout = useAuthStore((state) => state.logout)
    const navigate = useNavigate()
    const id = useAuthStore(state => state.id)
    const [keyword, setKeyword] = useState('')

    async function handleLogout() {
        await fetch('/api/v1/auth/logout', {
            method: 'POST',
            credentials: 'include',
        })
        logout()
        navigate('/')
    }

    function handleSearch(e: React.FormEvent) {
        e.preventDefault()
        navigate(`/search?keyword=${encodeURIComponent(keyword)}`)
    }

  return (
    <header>
        <div className="p-3 text-center bg-white border-bottom">
            <div className="container">
            <div className="row gy-3">
                <div className="col-lg-2 col-sm-4 col-4">
                {/* In production with basename='/new' this will resolve to /new/ automatically */}
                <Link to="/" className="float-start">
                <img src="https://cdn-icons-png.flaticon.com/256/13410/13410905.png" height="35" />
                </Link>
                </div>
                <div className="order-lg-last col-lg-4 col-sm-6 col-6">
                <div className="d-flex align-items-center gap-1 flex-wrap">
                    {logged ? <>
                        <button className="btn btn-primary btn-nowrap" onClick={handleLogout}>Log Out</button>
                        <Link to="/publish" className="btn btn-primary btn-sm d-flex align-items-center"><i className="fas fa-tag me-2"></i><span>Sell</span></Link>
                        <Link to="/my_listings" className="btn btn-outline-primary btn-sm d-flex align-items-center"><i className=" fas fa-box me-2"></i><span>My items</span> </Link>
                        <Link to="/my_deals" className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-shopping-cart me-2"></i><span>My deals</span> </Link> 
                        {role === 'ADMIN' &&
                            <Link to="/administrator" className="btn btn-dark btn-sm d-flex align-items-center"><i className="fas fa-user-shield me-2"></i><span>admin</span> </Link>
                        }
                        <Link to={`/user_account/${id}`} className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-user"></i><span>My account</span> </Link> 
                        </> 
                    : <>
                        <Link to="/login" className="btn btn-outline-dark btn-sm d-flex align-items-center"><i className="fas fa-user-alt me-2"></i><span>Log in</span></Link>
                        <Link to="/register" className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-user-plus me-2"></i><span>Register</span> </Link>
                    </>
                    }    
                </div>
        </div>
        <div className="col-lg-5 col-md-12 col-12">
            <form onSubmit={handleSearch}>
                <div className="input-group float-center" style={{ maxWidth: '500px' }}>
                <input 
                        type="search" 
                        className="form-control" 
                        placeholder="Search"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                />
                 <button type="submit" className="btn btn-primary shadow-0">
                        <i className="fas fa-search"></i>
                </button>
                </div>
            </form>
            </div>
            </div>
            </div>
        </div>
        
        <nav className="navbar navbar-expand-lg navbar-light" style={{ backgroundColor: '#f5f5f5' }}>
            <div className="container justify-content-center justify-content-md-between">
            <button
                    className="navbar-toggler border text-dark py-2"
                    type="button"
                    data-mdb-toggle="collapse"
                    data-mdb-target="#navbarLeftAlignExample"
                    aria-controls="navbarLeftAlignExample"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                    >
                <i className="fas fa-bars"></i>
            </button>

            <div className="collapse navbar-collapse" id="navbarLeftAlignExample">
                <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Clothing">Clothing</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Electronics">Electronics</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Home">Home</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Sports">Sports</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Books">Books</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link text-dark" to="/search?category=Others">Others</Link>
                </li>
                </ul>
            </div>
            </div>
        </nav>
        </header>
  )
}

export default Header

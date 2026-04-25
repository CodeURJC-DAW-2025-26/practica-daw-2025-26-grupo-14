import { useAuthStore } from "./authStore"
import "./styles.css"


function Header() {
    const logged = useAuthStore((state) => state.logged)
    const role = useAuthStore((state) => state.role)
  return (
    <header>
        <div className="p-3 text-center bg-white border-bottom">
            <div className="container">
            <div className="row gy-3">
                <div className="col-lg-2 col-sm-4 col-4">
                <a href="/" className="float-start">
                    <img src="https://cdn-icons-png.flaticon.com/256/13410/13410905.png" height="35" />
                </a>
                </div>


                <div className="order-lg-last col-lg-4 col-sm-6 col-6">
                <div className="d-flex align-items-center gap-1 flex-wrap">
                    {logged ? <>
                        <form action="/logout" method="post" className="d-inline">
                            <button className="btn btn-primary btn-nowrap" type="submit">Log Out</button>
                            <input type="hidden" name="_csrf" value="{{token}}" />
                        </form>
                        <a href="/publish" className="btn btn-primary btn-sm d-flex align-items-center"><i className="fas fa-tag me-2"></i><span>Sell</span></a>
                        <a href="/my_listings" className="btn btn-outline-primary btn-sm d-flex align-items-center"><i className=" fas fa-box me-2"></i><span>My items</span> </a>
                        <a href="/my_deals" className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-shopping-cart me-2"></i><span>My deals</span> </a> 
                        {role === 'ADMIN' &&
                            <a href="/administrator" className="btn btn-dark btn-sm d-flex align-items-center"><i className="fas fa-user-shield me-2"></i><span>admin</span> </a>
                        }
                        <a href="/user_account/{{myid}}" className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-user"></i><span>My account</span> </a> 
                        </> 
                    : <>
                        <a href="/login" className="btn btn-outline-dark btn-sm d-flex align-items-center"><i className="fas fa-user-alt me-2"></i><span>Log in</span></a>
                        <a href="/register" className="btn btn-outline-secondary btn-sm d-flex align-items-center"><i className="fas fa-user-plus me-2"></i><span>Register</span> </a>
                    </>
                    }    
                </div>
                </div>
                
                
            <div className="col-lg-5 col-md-12 col-12">
                <form action="/search" method="get">
                    <div className="input-group float-center" style={{ maxWidth: '500px' }}>
                    
                    <input 
                        type="search" 
                        name="keyword" 
                        className="form-control" 
                        placeholder="Search" 
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
                    <a className="nav-link text-dark" href="/search?category=Clothing">Clothing</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-dark" href="/search?category=Electronics">Electronics</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-dark" href="/search?category=Home">Home</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-dark" href="/search?category=Sports">Sports</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-dark" href="/search?category=Books">Books</a>
                </li>
                <li className="nav-item dropdown">
                    <a className="nav-link dropdown-toggle text-dark mb-0"
                    href="/search?category=Others"
                    id="navbarDropdown"
                    role="button"
                    data-mdb-toggle="dropdown"
                    aria-expanded="false">
                    Others
                    </a>

                    <ul className="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li>
                        <a className="dropdown-item" href="#">Action</a>
                    </li>
                    <li>
                        <a className="dropdown-item" href="#">Another action xddd</a>
                    </li>
                    <li><hr className="dropdown-divider" /></li>
                    <li>
                        <a className="dropdown-item" href="#">Something else here</a>
                    </li>
                    </ul>
                </li>
                </ul>
            </div>
            </div>
        </nav>
        </header>
  )
}

export default Header
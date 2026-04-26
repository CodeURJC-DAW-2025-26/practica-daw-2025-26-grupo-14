import { useSearchParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { searchProducts, type ProductsState } from './products'


function search() {
    const [searchParams] = useSearchParams() //, setSearchParams


    const keyword = searchParams.get('keyword') ?? ''
    const category = searchParams.get('category') ?? ''
    //const minPrice = searchParams.get('minPrice') ?? ''
//const maxPrice = searchParams.get('maxPrice') ?? ''
//const minSellerRate = searchParams.get('minSellerRate') ?? ''
    
    const [prod, setProd] = useState<ProductsState>({
            products: [],
            pageNumber: 0,
            next:true,
            })
    

    async function loadMoreSearch() {
        if (!prod.next) {
            return
        }

        const response = await searchProducts({
            keyword,
            category,
            page: prod.pageNumber,
        })

        if (response.length === 0) {
            setProd((currentState) => ({
                products: [...currentState.products],
                pageNumber: currentState.pageNumber,
                next: false,
            }))
            return
        }
        console.log(response)

        setProd((currentState) => ({
            products: [...currentState.products, ...response],
            pageNumber: currentState.pageNumber + 1,
            next: true,
        }))
    }
    
    useEffect(() => {
            loadMoreSearch()
        }, [])
    

    return( 
        <>
            <section className="">
            <div className="container">
              <div className="row">
                
                <div className  ="col-lg-3">
                
                    <div className = "card p-3 mb-4">
                        <h5>Filter by</h5>

                        {/* <div className="mb-3">
                            <label className= "form-label">Min Price</label>
                            <input type="number" className="form-control mb-3" id="minPrice" name="minPrice" placeholder="0"/>
                            <label className= "form-label">Max Price</label>
                            <input type="number" className="form-control mb-3" id="maxPrice" name="maxPrice" placeholder="1000"/>
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Minimun Seller Rate</label>
                            <select id="minSellerRate" className="form-select">
                            <option value="">⭐+</option>
                            <option value="1">⭐⭐+</option>
                            <option value="2">⭐⭐⭐+</option>
                            <option value="3">⭐⭐⭐⭐+</option>
                            <option value="4">⭐⭐⭐⭐⭐</option>
                            </select>
                        </div>
                        <button id="price-filter-btn" className="btn btn-outline-secondary w-100">Apply</button>*/}
                    </div>
                
                    
                    <button
                            className="btn btn-outline-secondary mb-3 w-100 d-lg-none"
                            type="button"
                            data-mdb-toggle="collapse"
                            data-mdb-target="#navbarSupportedContent"
                            aria-controls="navbarSupportedContent"
                            aria-expanded="false"
                            aria-label="Toggle navigation"
                            >
                        <span>Show filter</span>
                    </button>

                </div>
                
                
                <div className="col-lg-9">
                    <header className="d-sm-flex align-items-center border-bottom mb-4 pb-3">
                    <strong className="d-block py-2">Items found </strong>
                    
                    </header>

                    <div id="products_div" className="row">
                    

                    {prod != null && prod.products.map((p) => (
                        <div className="col-lg-4 col-md-6 col-sm-6 d-flex" key={p.id}>
                            <div className="card w-100 my-2 shadow-2-strong">
                                 <a href="/product/${p.id}">
                                <img src="${p.imageId ? '/images/' + p.imageId : '/images/noImage.png'}" className="card-img-top"/>
                                </a>
                                <div className="card-body">
                                    <h5>${p.name}</h5>
                                    <p>${p.shortDescription ?? ''}</p>
                                    <h6>${p.price} €</h6>
                                </div>
                            </div>
                        </div>
                    ))}
                    {prod.next && <button onClick={loadMoreSearch} className="btn btn-outline-dark">load more</button>}
                    

                    </div>

                    
                </div>
              </div>
            </div>            
            </section>
    </>)
}

export default search
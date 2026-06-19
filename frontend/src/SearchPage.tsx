import { Link, useSearchParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { searchProducts, type ProductsState } from './components/products'
import noImage from './assets/noImage.png'

function SearchPage() {
    const [searchParams] = useSearchParams()

    const keyword = searchParams.get('keyword') ?? ''
    const category = searchParams.get('category') ?? ''
    
    const [prod, setProd] = useState<ProductsState>({
        products: [],
        pageNumber: 0,
        next: true,
    })
    
    async function loadMoreSearch() {
        if (!prod.next) return

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
            <section>
            <div className="container">
              <div className="row">
                
                <div className="col-lg-3">
                    <div className="card p-3 mb-4">
                        <h5>Filter by</h5>
                    </div>
                </div>
                
                <div className="col-lg-9">
                    <header className="d-sm-flex align-items-center border-bottom mb-4 pb-3">
                        <strong className="d-block py-2">Items found</strong>
                    </header>

                    <div className="row">
                        {prod.products.map((p) => (
                            <div className="col-lg-4 col-md-6 col-sm-6 d-flex" key={p.id}>
                                <div className="card w-100 my-2 shadow-2-strong">
                                    <Link to={`/product/${p.id}`}>
                                        <img 
                                            src={p.imageIds?.[0] ? `/images/${p.imageIds[0]}` : noImage} 
                                            className="card-img-top" 
                                        />
                                    </Link>
                                    <div className="card-body">
                                        <h5>{p.name}</h5>
                                        <p>{p.shortDescription ?? ''}</p>
                                        <h6>{p.price} €</h6>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {prod.next && (
                        <div className="text-center mt-3">
                            <button onClick={loadMoreSearch} className="btn btn-outline-dark">
                                Load more
                            </button>
                        </div>
                    )}
                </div>
              </div>
            </div>            
            </section>
        </>
    )
}

export default SearchPage

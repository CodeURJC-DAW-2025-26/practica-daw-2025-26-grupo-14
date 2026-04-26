
import { useEffect, useState } from "react"
import { useAuthStore } from "./authStore"
import { fetchLocalProducts, fetchProductsPage } from "./products"
import type { Product, ProductsState} from "./products"



function main() {
    const city = useAuthStore((state) => state.city)
    const [localProd, setLocalProd] = useState<ProductsState>({
        products: [],
        pageNumber: 0,
        next:true,
        })

    async function loadMoreLocal() {
    if (!localProd.next) {
        return
    }

    if (!city) {
        setLocalProd({
        products: [],
        pageNumber: 0,
        next:false,
        })

    }else {

    const data = await fetchLocalProducts(city, localProd.pageNumber)

    if (data.length === 0) {
        setLocalProd((currentState) => ({
        products: [...currentState.products],
        pageNumber: currentState.pageNumber,
        next: false,
    }))
    }

    setLocalProd((currentState) => ({
        products: [...currentState.products, ...data],
        pageNumber: currentState.pageNumber + 1,
        next: true,
    }))}
    }

    const [recommendedProd, setRecommendedProd] = useState<ProductsState>({
            products: [],
            pageNumber: 0,
            next:true,
            })

    async function loadMoreRecommended() {
    const data = await fetchProductsPage(recommendedProd.pageNumber)

    if (data.length === 0) {
        setRecommendedProd((currentState) => ({
            products: [...currentState.products],
            pageNumber: currentState.pageNumber,
            next: false,
        }))
        return
    }

    setRecommendedProd((currentState) => ({
        products: [...currentState.products, ...data],
        pageNumber: currentState.pageNumber + 1,
        next: true,
    }))
    }

    useEffect(() => {
        loadMoreRecommended()
    }, [])

    useEffect(() => {
        if (city) {
        loadMoreLocal()
        }
    }, [city])

    return (    
        <>
            <section className="pt-3">
            <div className="container">
                <div className="row gx-3">
                <main className="col-lg-9">
                    <div className="card-banner p-5 bg-primary rounded-5" style={{ height: '350px' }}>
                    <div style={{ maxWidth: '500px' }}>
                        <h2 className="text-white">
                        Everything you need, for less.
                        </h2>
                        <p className="text-white">Discover what you’ve been looking for at unbeatable prices on our marketplace, where users sell the things they don’t use anymore.</p>
                        <a href="#" className="btn btn-light shadow-0 text-primary"> View more </a>
                    </div>
                    </div>
                </main>
                <aside className="col-lg-3">
                    <div className="card-banner h-100 rounded-5" style={{ backgroundColor: '#f87217' }}>
                    <div className="card-body text-center pb-5">
                        <h5 className="pt-5 text-white">Don’t use it anymore? Sell it.</h5>
                        <p className="text-white">oin our website and start selling your unused items easily.</p>
                        <a href="#" className="btn btn-light shadow-0 text-primary"><i className="fas fa-user-alt m-1 me-md-2"></i> Sign in </a>
                    </div>
                    </div>
                </aside>
                </div>
            </div>
            </section>


            {useAuthStore().city && localProd!=null && localProd.products.map((product : Product) => ( <>
                <section>
                <div className="container my-5">
                    <header className="mb-4">
                    <h3>📍 In your city</h3> {/* Change to carousel if you feel like it */}
                    </header>

                    <div className="position-relative">

                    <div id="local-products-row" className="d-flex flex-row overflow-auto px-5">
                        <div className="card me-3 shadow-sm border rounded-2 flex-shrink-0" style={{ minWidth: '220px', border: '1px solid #ccc' }}>
                        <a href={product.imageIds?.[0]
                                ? `/images/${product.imageIds[0]}`
                                : '/my_images/noImage.png'
                            }>
                            
                        </a>
                        <div className="card-body p-2">
                            <a href="#!" className="btn btn-light border px-2 pt-2 float-end icon-hover">
                            <i className="fas fa-heart fa-lg px-1 text-secondary"></i>
                            </a>
                            <h5 className="card-title">${product.price.toFixed(2)} €</h5>
                            <p className="card-text mb-0">{product.name}</p>
                        </div>
                        </div>
                    </div>
                    
                    </div>
                </div>
                </section>
            </>))}
            {useAuthStore().city && localProd.next  && (
                <div className="text-center mt-3">
                <button onClick={() => loadMoreLocal()} className="btn btn-outline-dark">Load more</button>
                </div>
            )}

            {recommendedProd!= null && recommendedProd.products.length > 0 &&  <>
            <section>
            <div className="container my-5">
                <header className="mb-4"> {/* Change one next to the other */ }
                <h3>Recommended for You</h3>
                </header>   
             
                { recommendedProd.products.map((product: Product) => (
                    <div className="col-lg-3 col-md-6 col-sm-6" key={product.id}>
                        <div className="card my-2 shadow-0">
                            <a href={`/product/${product.id}`}>
                                <img src={product.imageIds?.[0] ? `/images/${product.imageIds[0]}` : '/my_images/noImage.png'} className="card-img-top rounded-2" style={{ aspectRatio: '1/1' }} />
                            </a>
                            <div className="card-body p-0 pt-3">
                                <h5 className="card-title">{product.price.toFixed(2)} €</h5>
                                <p className="card-text mb-0">{product.name}</p>
                                <p className="text-muted">{product.shortDescription ?? ''}</p>
                            </div>
                        </div>
                    </div>
                ))}
                </div>
                </section>
            </>}
            
            {recommendedProd.next && <>
                <div id="recommended-row" className="row"></div>
                <div className="text-center mt-3">
                    <button onClick={() => loadMoreRecommended()} className="btn btn-outline-dark">Load more</button>
                </div>
            </>}

        </> 
    )
}

export default main
export type Product = {
  category: String,
  condition: String,
  contactPreference: String,
  createdAt: Date,
  fullDescription: Text,
  id: number,
  imageIds: [number],
  name: String,
  price: number,
  reported: boolean,
  reportedMessage: string,
  sellerId: number,
  shortDescription: string
}


export type ProductsState = {
  products: Product[]
  pageNumber: number
  next: boolean
}

export function getFirstImageId(product: Product): number | null {
  return product.imageIds.length > 0 ? product.imageIds[0] : null
}

export function getProductImageUrl(product: Product): string {
  const firstImageId = getFirstImageId(product)
  return firstImageId ? `/images/${firstImageId}` : '/my_images/noImage.png'
}

export function appendProducts(current: Product[], next: Product[]): Product[] {
  return [...current, ...next]
}

export async function fetchProductsPage(
  page: number
): Promise<Product[]> {
  const response = await fetch(`http://localhost:8080/api/v1/products?page=${page}`)

  if (!response.ok) {
    throw new Error('Error loading products page')
  }

  return response.json()
}

export async function fetchLocalProducts(
  city: string,
  page: number
): Promise<Product[]> {
  const response = await fetch(
    `http://localhost:8080/api/v1/products?city=${encodeURIComponent(city)}&page=${page}`
  )

  if (!response.ok) {
    throw new Error('Error loading local products')
  }

  return response.json()
}

type SearchProductsParams = {
  keyword?: string
  category?: string
  page?: number
}

export async function searchProducts({
  keyword,
  category,
  page = 0,
}: SearchProductsParams): Promise<Product[]> {
  const params = new URLSearchParams()

  if (keyword) {
    params.set('keyword', keyword)
  }

  if (category) {
    params.set('category', category)
  }

  params.set('page', page.toString())

  const response = await fetch(`http://localhost:8080/api/v1/products?${params.toString()}`)

  if (!response.ok) {
    throw new Error('Error searching products')
  }

  return response.json()
}


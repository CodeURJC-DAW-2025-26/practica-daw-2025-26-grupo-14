import { useEffect, useState } from 'react'
import type { Product } from './products';
import { Link } from 'react-router-dom';

function AdminListingsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [sellerMap, setSellerMap] = useState<Record<number, { name: string }>>({});

  useEffect(() => {
    // Simulate fetching reported products
    const fetchReportedProducts = async () => {
      // Replace this with your actual API call
      const response = await fetch('/api/v1/products');
      const data: Product[] = await response.json();
      const reportedProducts = data.filter((product: Product) => product.reportedMessage);
      setProducts(reportedProducts);

      // Simulate fetching seller information
      const sellerIds = reportedProducts.map((product) => product.sellerId);
      const uniqueSellerIds = [...new Set(sellerIds)];
      const sellerPromises = uniqueSellerIds.map((id) =>
        fetch(`/api/v1/sellers/${id}`).then((res) => res.json())
      );
      const sellers = await Promise.all(sellerPromises);
      const sellerRecord: Record<number, { name: string }> = {};
      sellers.forEach((seller) => {
        sellerRecord[seller.id] = { name: seller.name };
      });
      setSellerMap(sellerRecord);
    };

    fetchReportedProducts();
  }, []);

  return (<>
    <div className="container-fluid">
        <div className="row">
          <aside className="col-md-2 p-0" style={{ background: '#212529', minHeight: '100vh' }}>
            <h4 className="text-white text-center py-3 border-bottom">Admin Panel</h4>
            <Link
              to="/administrator"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/administrator' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/administrator' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-chart-line me-2"></i>Dashboard
            </Link>
            <Link
              to="/admin_users"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_users' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_users' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-users me-2"></i>Users
            </Link>
            <Link
              to="/admin_listings"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_listings' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_listings' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-flag me-2"></i>Moderation
            </Link>
            <Link
              to="/admin_stats"
              className={`d-block text-decoration-none py-3 px-3 ${location.pathname === '/admin_stats' ? 'bg-primary text-white' : 'text-muted'}`}
              style={{ color: location.pathname === '/admin_stats' ? '#fff' : '#adb5bd' }}
            >
              <i className="fas fa-chart-pie me-2"></i>Statistics
            </Link>
            <a href="/" className="d-block text-decoration-none py-3 px-3 text-muted" style={{ color: '#adb5bd' }}>
              <i className="fas fa-sign-out-alt me-2"></i>Home
            </a>
          </aside>

        <main className="col-md-10 p-4">
            <div className="col-md-10 p-4">
            <h2 className="mb-4">Reported Listings</h2>
            <div className="card shadow-sm">
                <div className="card-body">
                <table className="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Item</th>
                        <th>Seller</th>
                        <th>Reason</th>
                        <th className="text-end">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {products.map((product) => (
                        <tr>
                        <td>{product.name}</td>
                        <td>{sellerMap[product.sellerId]?.name ?? 'Unknown seller'}</td>
                        <td>{product.reportedMessage}</td>
                        <td className="text-end">
                            <form action="/deleteproduct/{{id}}" method="post" style={{ display: 'inline' }}>
                            <input type="hidden" name="_csrf" value="{{token}}"/>
                            <button type="submit" className="btn btn-danger btn-sm">Remove</button>
                            </form>
                            <a href="/ignore_report/{{id}}" className="btn btn-outline-secondary btn-sm">Ignore</a>
                        </td>
                        </tr>
                    ))}

                    </tbody>
                </table>

                {products.length === 0 && (
                  <p> Not products yet. </p>
                )}
                </div>
            </div>
            </div>
        </main>
        </div>
        </div>
  </>);
}

export default AdminListingsPage
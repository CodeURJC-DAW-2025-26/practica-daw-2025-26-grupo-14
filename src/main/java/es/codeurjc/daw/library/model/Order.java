package es.codeurjc.daw.library.model;

//import java.util.List;

//import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

    @ManyToOne
    private Product product;

    private String state;


	/*@OneToMany
	private Image image;
    //
	@ManyToMany
	private List<Shop> shops;*/

	public Order() {
	}

	public Order(String state, Product product) {
		super();
		this.state = state;
		this.product = product;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}



	/*public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}*/

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}*/

	@Override
	public String toString() {
		return "Order [id=" + id + ", state=" + state + ", product=" + product + "]";
	}
}


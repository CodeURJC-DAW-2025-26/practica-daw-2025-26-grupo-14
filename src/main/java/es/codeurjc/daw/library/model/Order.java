package es.codeurjc.daw.library.model;

//import java.util.List;

import jakarta.validation.constraints.NotBlank;

//import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

    @ManyToOne
    private Product product;

	@NotBlank(message = "state is required")
    private String state;

	@OneToOne
	@JsonManagedReference
	private Rating rating;

	@ManyToOne
    private User buyer;


	/*@OneToMany
	private Image image;
    //
	@ManyToMany
	private List<Shop> shops;*/

	public Order() {
	}

	public Order(String state, Product product, User buyer) {
		super();
		this.state = state;
		this.product = product;
		this.buyer = buyer;
		this.rating = null;
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

	public User getBuyer() {
		return buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public User getSellUser() {
		return product.getSeller();
	}


	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Boolean isReserved() {
		return this.state.equals("Reserved");
	}

	public Boolean isAccepted() {
		return this.state.equals("Accepted");
	}

	public Boolean isOfferSent() {
		return this.state.equals("Offer sent");
	}

	public Boolean isCancelled() {
		return this.state.equals("Cancelled");
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


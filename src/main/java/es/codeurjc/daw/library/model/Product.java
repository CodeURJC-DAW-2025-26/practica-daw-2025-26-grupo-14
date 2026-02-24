package es.codeurjc.daw.library.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

    @ManyToOne
    private User seller;

    private String name;

    private float price;

	@Column(columnDefinition = "TEXT")
	private String shortDescription;

    @Column(columnDefinition = "TEXT")
	private String fullDescription;

    @ManyToMany
	private List<Rating> ratings;

	/*@OneToMany
	private Image image;
    //
	@ManyToMany
	private List<Shop> shops;*/

	public Product() {
	}

	public Product(String name, User seller, float price, String city, String description) {
		super();
		this.name = name;
		this.seller = seller;
		this.price = price;
		this.fullDescription = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}


	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getShortDescription() {
		return shortDescription;
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

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", seller=" + seller + ", price=" + price + ", shortDescription=" + shortDescription + "]";
	}
}

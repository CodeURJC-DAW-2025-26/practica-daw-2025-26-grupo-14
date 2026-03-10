package es.codeurjc.daw.library.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

    @ManyToOne
    private User seller;

	//private String city;

	//private String category;

	@NotBlank(message = "name is required")
	@Size(min = 2 , max = 35, message = "name must be between 2 an 35 characters")
    private String name;

	
    private float price;

	@NotBlank(message = "category is required")
	private String category;

	@NotBlank(message = "short description is required")
	@Size(min = 2 , max = 120, message = "short description must be between 2 an 120 characters")
	@Column(columnDefinition = "TEXT")
	private String shortDescription;

	@NotBlank(message = "description is required")
    @Column(columnDefinition = "TEXT")
	private String fullDescription;

    @ManyToMany
	private List<Rating> ratings;

	@NotBlank(message = "created at is required")
	private String createdAt;

	@Column(name="product_condition")
	private String condition;

	@NotBlank(message = "contact preference is required")
	private String contactPreference;

	@OneToMany
	private List<Image> images = new ArrayList<>();

	private Boolean reported = false;
	private String reportedMessage = "";
    

	public Product() {
	}

	public Product(String name, User seller, float price, String category, String condition, String description, String contactPreference) {
		super();
		this.name = name;
		this.seller = seller;
		this.price = price;
		this.condition = condition;
		this.contactPreference = contactPreference;
		this.category = category;
		this.fullDescription = description;
		this.shortDescription = description.length() > 100 ? description.substring(0, 100) + "..." : description;
		this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
	}

	//getters & setters
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

	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getCondition() {
		return condition;
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


	public Image getImage(int id) {
		return images.get(id);
	}

	public Image getFirstImage() {
    return (images != null && !images.isEmpty()) ? images.get(0) : null;
}

	public List<Image> getImages() {
		return images;
	}

	public void setImage(Image image) {
		if (image != null) this.images.add(image);
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public int getNumberImages(){
		return this.images.size();
	}

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

	public void setCondition(String condition) { 
		this.condition = condition; 
	}

	public void setContactPreference(String contactPreference) { 
		this.contactPreference = contactPreference; 
	}

	public String getContactPreference() { 
		return contactPreference;
	} 

	public void setReported(Boolean reported){
		this.reported = reported;
	}

	public void setReportedMessage(String message){
		this.reportedMessage = message;
	}

	public Boolean getReported(){
		return reported;
	}

	public String getReportedMessage(){
		return reportedMessage;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", seller=" + seller + ", price=" + price + ", shortDescription=" + shortDescription + "]";
	}

    public void setDate() {
		this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    } 
}

package es.codeurjc.daw.library.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	@NotBlank(message = "full name is required")
	@Size(min = 2 , max = 65, message = "full name must be between 2 an 65 characters")
	private String fullName;

	@NotBlank(message = "username is required")
	@Size(min = 2 , max = 20, message = "username must be between 2 an 20 characters")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "username can only contain letters, numbers and underscores")
    private String name;

	@NotBlank(message = "email is required")
	@Email(message = "email should be valid")
	private String email;

    private String city;

	@NotBlank(message = "password is required")
	@Size(min = 6, message = "password must be at least 6 characters long")
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

	@OneToOne
	private Image profilePicture;
    
	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
	private List<Product> myProducts;
    @OneToMany(mappedBy = "rated", cascade = CascadeType.ALL)
	private List<Rating> myRatings;
	@OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
	private List<Order> myOrders;

	private boolean isBanned;

	public User() {
	}

	public User(String fullName, String username, String email, String city, String password, String... roles) {
		super();
		this.fullName = fullName;
		this.name = username;
		this.email = email;
		this.city = city;
		this.password = password;
		this.isBanned = false;
		this.myProducts = new ArrayList<>();
		this.myRatings = new ArrayList<>();
		this.myOrders = new ArrayList<>();
		this.roles = List.of(roles);
	}

	public boolean getIsBanned() {
		return isBanned;
	}

	public void setIsBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Image getImage() {
		return profilePicture;
	}

	public void setImage(Image image) {
		this.profilePicture = image;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Product> getMyProducts() {
		return myProducts;
	}

	public void addProduct(Product product) {
		this.myProducts.add(product);
	}

	public List<Rating> getMyRatings() {
		return myRatings;
	}

	public void addRating(Rating rating) {
		this.myRatings.add(rating);
	}

	public List<Order> getMyOrders() {
		return myOrders;
	}

	public void addOrder(Order order) {
		this.myOrders.add(order);
	}

	public List<String> getRoles() {
    return roles;
	}

	public void setRoles(String... roles) {
		this.roles = List.of(roles);
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", fullName=" + fullName + ", username=" + name + ", email=" + email + ", city=" + city + "]";
	}

    public void setBanned(boolean b) {
        this.isBanned = b;
    }
}

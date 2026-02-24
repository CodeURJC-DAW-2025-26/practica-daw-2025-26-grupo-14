package es.codeurjc.daw.library.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	private String fullName;

    private String username;

    private String email;

    private String city;

	
	private String password;

	/*@OneToOne
	private Image profilePicture;
    //@Column(columnDefinition = "TEXT")*/
	@ManyToMany
	private List<Product> myProducts;
    @ManyToMany
	private List<Rating> myRatings;

	public User() {
	}

	public User(String fullName, String username, String email, String city, String password) {
		super();
		this.fullName = fullName;
		this.username = username;
		this.email = email;
		this.city = city;
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public List<Product> getMyProducts() {
		return myProducts;
	}

	public void setMyProducts(List<Product> myProducts) {
		this.myProducts = myProducts;
	}

	public List<Rating> getMyRatings() {
		return myRatings;
	}

	public void setMyRatings(List<Rating> myRatings) {
		this.myRatings = myRatings;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", fullName=" + fullName + ", username=" + username + ", email=" + email + ", city=" + city + "]";
	}
}

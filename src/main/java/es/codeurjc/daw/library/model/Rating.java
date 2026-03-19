package es.codeurjc.daw.library.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	@OneToOne(mappedBy = "rating")
	@JsonBackReference
	private Order order;

	@ManyToOne
	private User rater;

	@ManyToOne
	private User rated;

	@NotBlank(message = "summery is required")
	@Size(min = 2 , max = 120, message = "summery must be between 2 an 120 characters")
	private String summery;

	@Min(value = 1, message = "rating must be at least 1")
	@Max(value = 5, message = "rating must be at most 5")
	private int rating;

	@Column(columnDefinition = "TEXT")
	private String description;

	public Rating() {
	}

	public Rating(String summery, Order order, int rating, String description) {
		super();
		this.summery = summery;
		this.rater = order.getBuyer();
		this.rated = order.getSellUser();
		this.order = order;
		this.rating = rating;
		this.description = description;
	}

	public String getSummery() {
		return summery;
	}

	public void setSummery(String summery) {
		this.summery = summery;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getRater() {
		return rater;
	}

	public void setRater(User rater) {
		this.rater = rater;
	}

	public User getRated() {
		return rated;
	}

	public void setRated(User rated) {
		this.rated = rated;
	}

	public Order getOrder() {
		return order;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Rating [id=" + id + ", summery=" + summery + ", rater=" + rater + ", rating=" + rating + ", description=" + description + "]";
	}
}


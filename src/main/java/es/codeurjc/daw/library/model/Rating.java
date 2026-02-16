package es.codeurjc.daw.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;

	@OneToOne
	private User rater;

	private String summery;

	private int rating;

	@Column(columnDefinition = "TEXT")
	private String description;

	public Rating() {
	}

	public Rating(String summery, int rating, String description) {
		super();
		this.summery = summery;
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


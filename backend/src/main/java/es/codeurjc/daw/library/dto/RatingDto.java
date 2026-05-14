package es.codeurjc.daw.library.dto;

public class RatingDto {

    private Long id;
    private Long orderId;
    private Long raterId;
    private Long ratedId;
    private String summery;
    private int rating;
    private String description;

    public RatingDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRaterId() {
        return raterId;
    }

    public void setRaterId(Long raterId) {
        this.raterId = raterId;
    }

    public Long getRatedId() {
        return ratedId;
    }

    public void setRatedId(Long ratedId) {
        this.ratedId = ratedId;
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
}

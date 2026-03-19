package es.codeurjc.daw.library.dto;

public class ImageDto {

    private Long id;
    private String url;

    public ImageDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

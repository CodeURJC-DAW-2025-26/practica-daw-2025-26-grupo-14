package es.codeurjc.daw.library.dto;

import java.util.List;
import java.util.stream.Collectors;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Order;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCity(user.getCity());
        dto.setDni(user.getDni());
        dto.setRoles(user.getRoles());
        dto.setIsBanned(user.getIsBanned());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setProfilePictureId(user.getImage() != null ? user.getImage().getId() : null);
        return dto;
    }

    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSellerId(product.getSeller() != null ? product.getSeller().getId() : null);
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setShortDescription(product.getShortDescription());
        dto.setFullDescription(product.getFullDescription());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCondition(product.getCondition());
        dto.setContactPreference(product.getContactPreference());
        dto.setReported(product.getReported());
        dto.setReportedMessage(product.getReportedMessage());
        if (product.getImages() != null) {
            dto.setImageIds(product.getImages().stream().map(Image::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setProductId(order.getProduct() != null ? order.getProduct().getId() : null);
        dto.setBuyerId(order.getBuyer() != null ? order.getBuyer().getId() : null);
        dto.setState(order.getState());
        dto.setRatingId(order.getRating() != null ? order.getRating().getId() : null);
        return dto;
    }

    public static RatingDto toDto(Rating rating) {
        if (rating == null) {
            return null;
        }
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setOrderId(rating.getOrder() != null ? rating.getOrder().getId() : null);
        dto.setRaterId(rating.getRater() != null ? rating.getRater().getId() : null);
        dto.setRatedId(rating.getRated() != null ? rating.getRated().getId() : null);
        dto.setSummery(rating.getSummery());
        dto.setRating(rating.getRating());
        dto.setDescription(rating.getDescription());
        return dto;
    }

    public static ImageDto toDto(Image image) {
        if (image == null) {
            return null;
        }
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setUrl("/images/" + image.getId());
        return dto;
    }

    public static List<ProductDto> toProductDtoList(List<Product> products) {
        return products.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }

    public static List<OrderDto> toOrderDtoList(List<Order> orders) {
        return orders.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }

    public static List<RatingDto> toRatingDtoList(List<Rating> ratings) {
        return ratings.stream().map(DtoMapper::toDto).collect(Collectors.toList());
    }
}

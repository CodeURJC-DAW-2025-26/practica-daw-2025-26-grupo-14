package es.codeurjc.daw.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;


@Component
public class SecurityUtil {

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtains the currently authenticated user
     * @return User entity of the authenticated user, or null if not authenticated
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (username == null || username.isEmpty()) {
            return null;
        }

        return userRepository.findByName(username).orElse(null);
    }

    /**
     * Checks if the authenticated user is an admin
     * @return true if user is authenticated and has ADMIN role
     */
    public boolean isAdmin() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return false;
        }
        return user.getRoles() != null && user.getRoles().contains("ADMIN");
    }

    /**
     * Checks if the authenticated user is the owner of a given user
     * @param userId ID of the user to check
     * @return true if authenticated user is admin or is the owner
     */
    public boolean isUserOwnerOrAdmin(Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }
        
        if (isAdmin()) {
            return true;
        }

        return authenticatedUser.getId().equals(userId);
    }

    /**
     * Checks if the authenticated user is the product seller or admin
     * @param sellerId ID of the seller
     * @return true if authenticated user is admin or is the seller
     */
    public boolean isProductOwnerOrAdmin(Long sellerId) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        return authenticatedUser.getId().equals(sellerId);
    }

    /**
     * Checks if the authenticated user is the order buyer or admin
     * @param buyerId ID of the buyer
     * @return true if authenticated user is admin or is the buyer
     */
    public boolean isOrderOwnerOrAdmin(Long buyerId) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        return authenticatedUser.getId().equals(buyerId);
    }

    /**
     * Checks if the authenticated user is the rater or admin
     * @param raterId ID of the rater
     * @return true if authenticated user is admin or is the rater
     */
    public boolean isRatingOwnerOrAdmin(Long raterId) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        return authenticatedUser.getId().equals(raterId);
    }
}

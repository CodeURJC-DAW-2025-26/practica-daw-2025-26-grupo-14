package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class reactController {
    
    @GetMapping({
        "/new",
        "/new/",
        "/new/search",
        "/new/login",
        "/new/register",
        "/new/publish",
        "/new/my_listings",
        "/new/my_deals",
        "/new/administrator",
        "/new/admin_users",
        "/new/admin_listings",
        "/new/admin_stats"
    })
    public String reactApp() {
        return "forward:/new/index.html";
    }

    @GetMapping({
        "/new/product/{id}",
        "/new/editproduct/{id}",
        "/new/create_rating/{orderId}",
        "/new/edit_rating/{ratingId}",
        "/new/chat/{orderId}",
        "/new/user_account/{id}",
        "/new/edituser/{id}"
    })
    public String reactAppPlus() {
        return "forward:/new/index.html";
    }
}

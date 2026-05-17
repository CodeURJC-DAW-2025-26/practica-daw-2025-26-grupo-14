package es.codeurjc.daw.library.security;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.daw.library.security.jwt.JwtRequestFilter;
import es.codeurjc.daw.library.security.jwt.UnauthorizedHandlerJwt;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
   
    @Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	@Order(1) // Ensure this filter chain is evaluated before any other filter chains
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**") // Apply this filter chain only to /api/** endpoints
				.authenticationProvider(authenticationProvider())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandlerJwt)) // Use custom entry point for unauthorized access
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints: always public
				.requestMatchers("/api/v1").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Products public read (GET) endpoints
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/**").permitAll()
				.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/users/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/ratings/**").permitAll()
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/charts/**").permitAll()
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/images/**").permitAll()
                // Create/modify products:Only authorized users (USER or ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/products/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/products/**").hasAnyRole("USER", "ADMIN")
				// See their conversations:Only authorized users (USER or ADMIN)
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/users/{id}/conversations/**").permitAll()
                // Orders: only authorized users (USER or ADMIN) can create orders, but anyone can read them (for simplicity, as they don't contain sensitive data in this example)
                .requestMatchers("/api/v1/orders/**").hasAnyRole("USER", "ADMIN")
                // Ratings: only authenticated users can create/modify ratings, but anyone can read them
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
                // Users management: only ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                // Any other API route: authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

	@Bean
	@Order(2) // Ensure this filter chain is evaluated after the API filter chain
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http

				.authenticationProvider(authenticationProvider())
				.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))// Disable CSRF for API endpoints, but keep it enabled for the rest of the application

				.authorizeHttpRequests(authorize -> authorize
						//STATIC RESOURCES
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers("/webjars/**").permitAll()
						.requestMatchers("/assets/**").permitAll() 
						.requestMatchers("/favicon.ico").permitAll()
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/images/**").permitAll()
						.requestMatchers("/my_images/**").permitAll()
						.requestMatchers("/products/**").permitAll() 
						.requestMatchers("/product/**").permitAll()
						.requestMatchers("/user_account/**").permitAll()
						.requestMatchers("/loginerror").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/search").permitAll()//allow search for everyone
						//API ENDPOINTS
						// requestMatchers("/api/**").permitAll()//allow API access for everyone, as it's read-only and doesn't expose sensitive data
						// PRIVATE PAGES
						.requestMatchers("/newproduct").hasAnyRole("USER")
						.requestMatchers("/editproduct").hasAnyRole("USER")
						.requestMatchers("/editproduct/*").hasAnyRole("USER")
						.requestMatchers("/edituser/**").hasAnyRole("USER")//allow users to edit their account, but not others
						.requestMatchers("/create_deal/*").hasAnyRole("USER")
						.requestMatchers("/removeproduct/*").hasAnyRole("ADMIN")
						.requestMatchers("/deleteproduct/*").hasAnyRole("USER")

						// Allow access to other pages (like login, logout, etc.)
						.anyRequest().authenticated())

				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
		
	}

	@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpConnector() {
    return factory -> {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        factory.addAdditionalConnectors(connector);
    };
}

}


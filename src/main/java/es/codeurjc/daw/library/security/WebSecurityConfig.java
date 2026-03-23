package es.codeurjc.daw.library.security;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
   
    @Autowired
	RepositoryUserDetailsService userDetailsService;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http.csrf(csrf -> csrf.disable());


		http
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
						.requestMatchers("/products/**").permitAll() 
						.requestMatchers("/product/**").permitAll()
						.requestMatchers("/user_account/**").permitAll()
						.requestMatchers("/loginerror").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/search").permitAll()//allow search for everyone
						//API ENDPOINTS
						.requestMatchers("/api/**").permitAll()//allow API access for everyone, as it's read-only and doesn't expose sensitive data
						//requestMatchers("/api/v1/admin/**").hasRole("ADMIN")restrict admin API endpoints to admins only
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


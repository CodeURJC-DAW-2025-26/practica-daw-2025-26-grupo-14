package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}


	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

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
							.requestMatchers(HttpMethod.GET, "/api/**").permitAll() // public read-only API
							.requestMatchers(HttpMethod.POST, "/api/users").permitAll() // registration
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
}

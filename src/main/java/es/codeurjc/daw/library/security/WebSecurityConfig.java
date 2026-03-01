package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
						.requestMatchers("/search").permitAll()//allow search for everyone
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

}

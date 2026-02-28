package es.codeurjc.daw.library.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

		User user = userRepository.findByName(name)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : user.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		return new org.springframework.security.core.userdetails.User(user.getName(), 
				user.getPassword(), roles);

	}
}

/*@GetMapping("/logout")
	public String logout() {
		return "main";
	}

	    	@Autowired
	private PasswordEncoder passwordEncoder;

		User seller1 = new User("Mario Martin", "Mario", "mario@martin.com", "Madrid", passwordEncoder.encode("pass"), "USER");
        User seller2 =  new User("Lucia Garcia", "LuGar", "lucia@garcia.com", "Barcelona", passwordEncoder.encode("pass"), "USER");
        User seller3 =  new User("Carlos Lopez", "Carlos", "carlos@lopez.com", "Valencia", passwordEncoder.encode("pass"), "USER");

	*/
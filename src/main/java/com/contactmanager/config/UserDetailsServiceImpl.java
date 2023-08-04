package com.contactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmanager.entities.User;
import com.contactmanager.repo.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.getUserByUserName(username);
		if (user.equals(null) || user == null) {
			throw new UsernameNotFoundException("User not found!");
		}
		return new CustomUserDetails(user);
	}

}

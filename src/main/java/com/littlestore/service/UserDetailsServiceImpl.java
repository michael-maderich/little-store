package com.littlestore.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlestore.entity.Customer;
import com.littlestore.entity.Role;

import java.util.Set;
import java.util.HashSet;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final CustomerService customerService;

	public UserDetailsServiceImpl(CustomerService customerService) {
		this.customerService = customerService;
	}

	// Returns UserDetails object containing username(email), password, and set of Authorities (using role.getName()) stored in grantedAuthorities
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = customerService.findByEmail(email);
		if (customer == null) throw new UsernameNotFoundException("User " + email + " was not found in the database!");
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (Role role : customer.getRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		}
		return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPassword(), grantedAuthorities);
	}

}

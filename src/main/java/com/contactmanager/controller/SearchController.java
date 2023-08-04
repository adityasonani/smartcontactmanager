package com.contactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.repo.ContactRepository;
import com.contactmanager.repo.UserRepository;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		System.out.println("Search query:: " + query);
		User user = userRepo.getUserByUserName(principal.getName());
		List<Contact> result = contactRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(result);
	}
}

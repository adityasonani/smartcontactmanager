package com.contactmanager.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUserId(@Param("userId") int userId, Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name, User user);

}

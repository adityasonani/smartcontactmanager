package com.contactmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Contact")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;

	@NotBlank(message = "Name is required!")
	@Size(min=3, message = "Minimum 3 characters required")
	private String name;

	@NotBlank(message = "Nickname is required!")
	@Size(min=2, message = "Minimum 2 characters required")
	private String secondName;

	private String work;

	@NotBlank(message = "Phone is required!")
	@Size(min=10,max=10, message = "Phone number should have 10 digits!")
	private String phone;
	
	@Email(message = "Must be a valid email!")
	@NotBlank(message = "Email is required!")
	private String email;

	private String imageUrl;

	@Column(length = 5000)
	@Size(min=0, max=5000, message = "Max 5000 characters allowed")
	private String description;

	@ManyToOne
	@JsonIgnore
	private User user;

	public Contact() {
		super();
	}

	public Contact(int cId, String name, String secondName, String work, String phone, String email, String imageUrl,
			String description, User user) {
		super();
		this.cId = cId;
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.phone = phone;
		this.email = email;
		this.imageUrl = imageUrl;
		this.description = description;
		this.user = user;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	@Override
//	public String toString() {
//		return "Contact [cId=" + cId + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", phone="
//				+ phone + ", email=" + email + ", imageUrl=" + imageUrl + ", description=" + description + ", user="
//				+ user + "]";
//	}
	
	
}

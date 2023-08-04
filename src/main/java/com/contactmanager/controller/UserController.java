package com.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;
import com.contactmanager.repo.ContactRepository;
import com.contactmanager.repo.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepo.getUserByUserName(userName);
		model.addAttribute("user", user);

		System.out.println("Logged in Username " + userName);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult br, Principal principal,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session) {
		model.addAttribute("title", "Add Contact");
		try {
			if (br.hasErrors()) {
				model.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}

			User user = userRepo.getUserByUserName(principal.getName());

			if (file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImageUrl("contact.png");
			} else {
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

				String newFileName = timeStamp + file.getOriginalFilename();

				contact.setImageUrl(newFileName);

				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newFileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image uploaded!");
			}

			contact.setUser(user);

			user.getContacts().add(contact);

			userRepo.save(user);

			// System.out.println("DATA CONTACT:: "+contact.toString());
			model.addAttribute("contact", new Contact());
			System.out.println("Contact saved!!!");
			session.setAttribute("message", new Message("Contact Saved Successfully!", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went wrong!" + e.getMessage(), "alert-danger"));
		}

		return "normal/add_contact_form";
	}

	@GetMapping("/view-contacts")
	public String viewContacts(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
			Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");

		User user = userRepo.getUserByUserName(principal.getName());

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepo.findContactsByUserId(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contacts.getTotalPages());

		return "normal/view_contact";
	}

	// showing particular contact details
	@RequestMapping("/contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID:: " + cId);

		model.addAttribute("title", "View Contact");

		try {
			Contact contact = contactRepo.getById(cId);

			User user = userRepo.getUserByUserName(principal.getName());

			if (contact != null && user.getId() == contact.getUser().getId())
				model.addAttribute("contact", contact);
		} catch (Exception e) {
			System.out.println("Exeption ::" + e.getMessage());
		}
		return "normal/contact_details";
	}

	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Principal principal, HttpSession session) {
		try {
			Contact contact = contactRepo.getById(cId);
			User user = userRepo.getUserByUserName(principal.getName());
			if (contact != null && user.getId() == contact.getUser().getId()) {
				contactRepo.delete(contact);
				String imgName = contact.getImageUrl();
				if (!imgName.equals("contact.png")) {
					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + imgName);
					Files.delete(path);
					System.out.println("Image deleted!");
				}
				session.setAttribute("message", new Message("Contact deleted successfully!", "alert-success"));
			} else {
				session.setAttribute("message", new Message("Can't delete the contact", "alert-danger"));
			}
		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong..", "alert-danger"));
		}
		return "redirect:/user/view-contacts";
	}

	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepo.getById(cId);
		User user = userRepo.getUserByUserName(principal.getName());
		if (contact != null && user.getId() == contact.getUser().getId())
			model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	@PostMapping("/process-update-contact")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {
		try {
			// image new?
			Contact prevContact = contactRepo.getById(contact.getcId());
			if (file.isEmpty()) {
				contact.setImageUrl(prevContact.getImageUrl());
			} else {
				//delete old image
				String prevImgName = prevContact.getImageUrl();
				if (!prevImgName.equals("contact.png")) {
					try {
						File saveFile = new ClassPathResource("static/images").getFile();
						Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + prevImgName);
						Files.delete(path);
						System.out.println("Old Image deleted!");
					} catch (Exception e) {
						System.err.println("exception while deleting old image");
					}
				}
				
				// set new image
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
				
				String newFileName = timeStamp + file.getOriginalFilename();
				
				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newFileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImageUrl(newFileName);

				System.out.println("Image updated!");
			}
			User user = userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepo.save(contact);
			session.setAttribute("message", new Message("Contact updated successfully!", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong..", "alert-danger"));
		}
		
		System.out.println("Contact to update:: " + contact.getName() + " " + contact.getcId());
		
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Your Profile");
		return "normal/profile";
	}

}

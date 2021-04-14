package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.UserService;

@RestController
@RequestMapping("/agencyClient")
public class UserRestController {
	
	@Autowired
	UserService userService;
	
	@PutMapping(value = "/affecterClientAAgency/{userId}/{id}") 
	public void affecterClientAAgency(@PathVariable("userId") long userId, @PathVariable("id") long id) {
		userService.affecterClientAAgency(userId, id);
	}

}

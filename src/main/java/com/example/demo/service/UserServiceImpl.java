package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Agency;
import com.example.demo.entity.User;
import com.example.demo.repository.AgencyRepository;
import com.example.demo.repository.UserRepository;



@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	AgencyRepository agencyRepository;
	
	@Autowired
	UserRepository userRepository;

	@Override
	public void affecterClientAAgency(long userId, long id) {
		
		
		 Agency	agency=agencyRepository.findById(id).orElse(null);
		   User	 client =userRepository.findById(userId).orElse(null);
		   List<User> le =new ArrayList <User>();
			le.add(client);
			agency.setUsers(le);
			agencyRepository.save(agency);	
		
	}
	
	

}

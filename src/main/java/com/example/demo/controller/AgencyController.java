package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import com.example.demo.entity.Agency;
import com.example.demo.service.AgencyService;

@Controller

public class AgencyController {
	
	@Autowired
	private AgencyService agencyService;
	
	@Autowired
	private ServletContext context;
	
	//display list of agencies
	@GetMapping("/")
	public String viewHomePage(Model model, @Param("keyword") String keyword){
		List <Agency> listAgencies = agencyService.searchAgency(keyword);
		model.addAttribute("listAgencies", listAgencies);
		model.addAttribute("keyword", keyword);
		return "index";
	}
	
	@GetMapping("/showNewAgencyForm")
    public String showNewAgencyForm(Model model){
    	Agency agency = new Agency();
    	model.addAttribute("agency", agency);
    	return "new_agency";
    	
    }
	
	@PostMapping("/saveAgency")
	public String saveAgency(@ModelAttribute("agency") Agency agency){
		//save agency to BD
		agencyService.addAgency(agency);
		return "redirect:/";
		
	}
	
	@GetMapping("/showFormForUpdate/{id}")
	public String showFormForUpdate(@PathVariable( value = "id") long id, Model model){
		//get agency from the service
    	Agency agency = agencyService.getById(id);
    	
    	//set agency as a model attribute to pre-populate the form
    	model.addAttribute("agency", agency);
    	return "update_agency";
		
	}
	
	@GetMapping("/deleteAgency/{id}")
    public String deleteAgency(@PathVariable (value = "id") long id){
    	
    	//delete agency method
    	this.agencyService.deleteAgency(id);;
    	return "redirect:/";
    }
	
	@GetMapping("/createPdf")
	public void createPdf (HttpServletRequest request, HttpServletResponse response){
		List <Agency> agencies = agencyService.findAllAgency();
		boolean isFlag = agencyService.createPdf(agencies, context, request, response);
		 if(isFlag){
	        	String fullPath= request.getServletContext().getRealPath("/resources/reports/"+"agencies"+".pdf");
	        	filedownload(fullPath, response,"agencies.pdf");
	        }
	}
	
	@GetMapping("/createExcel")
	public void createExcel(HttpServletRequest request, HttpServletResponse response){
		List <Agency> agencies = agencyService.findAllAgency();
		boolean isFlag = agencyService.createExcel(agencies, context, request, response);
		if(isFlag){
        	String fullPath= request.getServletContext().getRealPath("/resources/reports/"+"agencies"+".xls");
        	filedownload(fullPath, response,"agencies.xls");
        }
	}

	private void filedownload(String fullPath, HttpServletResponse response, String fileName) {
		
		File file = new File(fullPath);
		final int BUFFER_SIZE =4096;
		
		if(file.exists()){
			try{
				FileInputStream inputStream =new FileInputStream(file);
				String mimeType =context.getMimeType(fullPath);
				response.setContentType(mimeType);
				response.setHeader("content-disposition", "attachement; fileName="+ fileName);
				OutputStream outputStream = response.getOutputStream();
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				while((bytesRead = inputStream.read(buffer))!= -1){
					outputStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outputStream.close();
				file.delete();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		
	}

}
}

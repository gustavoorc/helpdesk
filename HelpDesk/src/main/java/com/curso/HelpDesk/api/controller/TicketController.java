package com.curso.HelpDesk.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curso.HelpDesk.api.entidade.ChangeStatus;
import com.curso.HelpDesk.api.entidade.Ticket;
import com.curso.HelpDesk.api.entidade.User;
import com.curso.HelpDesk.api.enums.ProfileEnum;
import com.curso.HelpDesk.api.enums.StatusEnum;
import com.curso.HelpDesk.api.response.Response;
import com.curso.HelpDesk.api.security.jwt.JwtTokenUtil;
import com.curso.HelpDesk.api.service.TicketService;
import com.curso.HelpDesk.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins ="*")
public class TicketController {

	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping()
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> create (HttpServletRequest request, @RequestBody Ticket ticket, 
				BindingResult result){
		Response<Ticket> response = new Response<Ticket>();
		
		try {
			validateCreateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(StatusEnum.getStatus("New"));
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			Ticket ticketPersist = (Ticket) ticketService.createOrUpdate(ticket);
			response.setData(ticketPersist);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	
	
	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}



	private User userFromRequest(HttpServletRequest request) {
	String token = request.getHeader("Authorization");
	String email = jwtTokenUtil.getUsernameFromToken(token);
		return userService.findByEmail(email);
	}



	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Title no information"));
		}
	}
	
	@PutMapping()
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result) {
		Response<Ticket> response = new Response<Ticket>();
		try {
			validateUpdateTicket(ticket, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			Optional<Ticket> ticketCurrentOptional = ticketService.findById(ticket.getId());
			Ticket ticketCurrent = ticketCurrentOptional.get();
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setDate(new Date());
			ticket.setNumber(ticketCurrent.getNumber());
			if (ticketCurrent.getAssignedUser() != null) {
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			}
			Ticket tickedPersist = (Ticket) ticketService.createOrUpdate(ticket);
			response.setData(tickedPersist);

		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}


	private void validateUpdateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getId() == null) {
			result.addError(new ObjectError("Ticket", "Id no information"));
		}if(ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Title no information"));
		}
		
	}
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER,TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id) {
		Response<Ticket> response = new Response<Ticket>();
		
		Optional<Ticket> ticketOptional = ticketService.findById(id);
		Ticket ticket = ticketOptional.get();
		if(ticket == null) {
			response.getErrors().add("Register Not Found id:"+ id);
			return ResponseEntity.badRequest().body(response);
		}
		List<ChangeStatus> changes = new ArrayList<ChangeStatus>();
		Iterable<ChangeStatus> changeCurrent = ticketService.listChangeStatus(ticket.getId());
		for(Iterator<ChangeStatus>  iterator = changeCurrent.iterator(); iterator.hasNext(); ) {
			ChangeStatus changeStatus = (ChangeStatus) iterator.next();
			changeStatus.setTicket(null);
			changes.add(changeStatus);
		}
		ticket.setChanges(changes);
		response.setData(ticket);
		return ResponseEntity.ok(response);
		
	
	
	}
	
	
	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER'")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
		Response<String> response = new Response<String>();
		Optional<Ticket> ticketCurrentOptional = ticketService.findById(id);
		Ticket ticket = ticketCurrentOptional.get();
		if(ticket == null) {
			response.getErrors().add("Register Not Found id:"+ id);
			return ResponseEntity.badRequest().body(response);
		}
		
		ticketService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	
	}
	
	
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER,TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findById( HttpServletRequest request, @PathVariable("page") int page, @PathVariable("count") int count) {
		Response<Page<Ticket>> response = new Response<Page<Ticket>>();
		Page<Ticket> tickets = null;
		User userRequest = userFromRequest(request);
			if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
				tickets = ticketService.listTicket(page, count);
			}else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
				tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
			}
		response.setData(tickets);	
		
		return ResponseEntity.ok(response);
	
	}
	
	
}
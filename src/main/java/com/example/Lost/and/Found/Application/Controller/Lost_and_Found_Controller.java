package com.example.Lost.and.Found.Application.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Lost.and.Found.Application.Dto.Lostitem;
import com.example.Lost.and.Found.Application.Service.LostServiece;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class Lost_and_Found_Controller {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	LostServiece lostServiece;

	@GetMapping("/project")
	public String project() {
		return "Welcome to project";
	}

	@GetMapping("/home")
	public String home(@AuthenticationPrincipal UserDetails user) {
		String role = user.getAuthorities().iterator().next().getAuthority();
		return role.equals("ROLE_ADMIN") ? "Hey Admin Manage the items!" : " Hey User!!, report lost items!";
	}

	@PostMapping(value = "/Lost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Lostitem> uploadItem(@RequestPart("Lostitem") String lostItemJson,
			@RequestPart("imagefile") MultipartFile imagefile) {

		try {
			Lostitem lostitem = objectMapper.readValue(lostItemJson, Lostitem.class);
			Lostitem savedLostitem = lostServiece.uploadItem(lostitem, imagefile);
			return new ResponseEntity<>(savedLostitem, HttpStatus.CREATED);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallitemdetails")
	public ResponseEntity<List<Lostitem>> getdetails() {
		try {
			List<Lostitem> lost_items = lostServiece.getitemdetails();
			return new ResponseEntity<List<Lostitem>>(lost_items, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping("/delete/id/{id}")
	public ResponseEntity<String> deleteitembyid(@PathVariable int id) {
		try {
			lostServiece.deleteitembyid(id);
			return new ResponseEntity<String>("Item Deleted Sucessfully", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Item deletion failed: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping(value = "/updateitem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateItem(@RequestPart("lostitem") String lostitemJson,
			@RequestPart(value = "imagefile", required = false) MultipartFile imagefile) {
		try {
			Lostitem lostitem = objectMapper.readValue(lostitemJson, Lostitem.class);
			if (lostitem.getDatelost() == null) {
				return new ResponseEntity<>("datelost is required and must be in yyyy-MM-dd format",
						HttpStatus.BAD_REQUEST);
			}
			Lostitem updated = lostServiece.updateItem(lostitem, imagefile);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed: " + e.getMessage());
		}
	}
	
	@GetMapping("/reporter/{repoterName}")
	public ResponseEntity<?> getItemByRepoterName(@PathVariable String repoterName) {
	    try {
	        Optional<Lostitem> lostItem = lostServiece.findByRepoterName(repoterName);
	        if (lostItem.isPresent()) {
	            return ResponseEntity.ok(lostItem.get());
	        } else {
	            return ResponseEntity.status(404).body("No item found for reporter: " + repoterName);
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Error fetching item: " + e.getMessage());
	    }
	}
	

}

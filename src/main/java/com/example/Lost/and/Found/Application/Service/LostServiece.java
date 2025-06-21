package com.example.Lost.and.Found.Application.Service;

import java.io.IOException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.Lost.and.Found.Application.Dto.Lostitem;
import com.example.Lost.and.Found.Application.Dto.User;
import com.example.Lost.and.Found.Application.Repository.LostRepo;

@Service
public class LostServiece {
	@Autowired
	LostRepo lostRepo;

	public Lostitem uploadItem(Lostitem lostitem, MultipartFile imagefile) throws IOException {
		lostitem.setImagename(imagefile.getOriginalFilename());
		lostitem.setImagetype(imagefile.getContentType());
		lostitem.setImagedata(imagefile.getBytes());
		lostitem.setStatus("PENDING");
		return lostRepo.save(lostitem);
	}

	public List<Lostitem> getitemdetails() {
		try {
			List<Lostitem> lostitem = lostRepo.findAll();
			if (lostitem.isEmpty()) {
				throw new RuntimeException("No items found in DB");
			}
			return lostitem;
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch items: " + e.getMessage());
		}

	}

	public String deleteitembyid(int id) {
		if (lostRepo.existsById(id)) {
			lostRepo.deleteById(id);
			return "item deleted sucessfully";
		} else {
			throw new RuntimeException("Item not found in DB with ID: " + id);
		}
	}

	public Lostitem updateItem(Lostitem lostitem, MultipartFile imagefile) throws IOException {
		Optional<Lostitem> existingOptional = lostRepo.findById(lostitem.getId());
		if (existingOptional.isPresent()) {
			Lostitem existingItem = existingOptional.get();
			existingItem.setItemname(lostitem.getItemname());
			existingItem.setDescription(lostitem.getDescription());
			existingItem.setRepotername(lostitem.getRepotername());
			existingItem.setContactinfo(lostitem.getContactinfo());
			existingItem.setLocationlost(lostitem.getLocationlost());
			if (imagefile != null && !imagefile.isEmpty()) {
				existingItem.setImagename(imagefile.getOriginalFilename());
				existingItem.setImagetype(imagefile.getContentType());
				existingItem.setImagedata(imagefile.getBytes());
			}

			return lostRepo.save(existingItem);
		} else {
			throw new RuntimeException("Item not found with ID: " + lostitem.getId());
		}
	}

	public Optional<Lostitem> findByRepoterName(String repoterName) {
		try {
			Optional<Lostitem> lostitem = lostRepo.findByRepotername(repoterName);
			return lostitem;
		} catch (Exception e) {
			throw new RuntimeException("Failed to By Username: " + e.getMessage());
		}
	}

}

package com.example.Lost.and.Found.Application.Dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="lostitem")
@NoArgsConstructor
public class Lostitem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int id;

	private String repotername;
	private String itemname;
	private String description;
	private String locationlost;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate datelost;
	private String contactinfo;
	@Lob
	@JsonIgnore
	private byte[] imagedata;
	private String imagename;
	private String imagetype;
	private String status;
  
}

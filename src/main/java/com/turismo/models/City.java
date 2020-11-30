package com.turismo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * Model for a city register
 * 
 * @author Brayan Hernandez
 *
 */
@Entity
@Data
public class City {

	@Id
	private Integer id;
	@Column
	private String name;
	@Column
	private Integer population;
	@Column
	private String touristicPlace;
	@Column
	private String recommendedHotel;

}

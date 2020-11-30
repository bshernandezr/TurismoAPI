package com.turismo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * Model for a travel register
 * 
 * @author Brayan Hernandez
 *
 */
@Entity
@Data
public class Travel {

	@Id
	@GeneratedValue
	private Integer id;
	@Column
	private Integer idCity;
	@Column
	private String cityName;
	@Column
	private String idTourist;
	@Column
	private String travelDate;
}

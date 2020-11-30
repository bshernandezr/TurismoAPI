package com.turismo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;


/**
 * Model for a tourist register
 * 
 * @author Brayan Hernandez
 *
 */
@Entity
@Data
public class Tourist {

	@Id
	private String id;
	@Column
	private String name;
	@Column
	private String birthDate;
	@Column
	private String idType;
	@Column
	private int travelFrecuency;
	@Column
	private double travelBudget;
	@Column
	private String destination;
	@Column
	private Boolean creditCard;
	@Column
	private String gender;

}

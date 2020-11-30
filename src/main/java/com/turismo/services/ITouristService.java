package com.turismo.services;

import java.util.List;

import com.turismo.models.Tourist;

/**
 * Interface for Tourist services
 * 
 * @author Brayan Hernandez
 *
 */
public interface ITouristService {

	/**
	 * Read all the tourists in the table.
	 * 
	 * @return return a list with all the tourists registered
	 */
	List<Tourist> readAllTourists();

	/**
	 * Read the tourist that corresponds with the id joined.
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a tourist object, in other case
	 *         returns null
	 */
	Tourist readTouristById(String id);

	/**
	 * Delete the tourist that corresponds with the id entered
	 * 
	 * @param id String that corresponds with an id of a tourist
	 * @return Response for the request
	 */
	String deleteTourist(String id);

	/**
	 * Save a new register of a tourist.
	 * 
	 * @param tourist An object of tourist type that will be create in the tourist
	 *                table on the database
	 * @return Response for the request
	 */
	String saveTourist(Tourist tourist);

}

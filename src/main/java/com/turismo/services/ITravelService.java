package com.turismo.services;

import java.util.List;
import com.turismo.models.Travel;

/**
 * Interface for travel services
 * 
 * @author Brayan Hernandez
 *
 */
public interface ITravelService {

	/**
	 * Read all the travels in the table.
	 * 
	 * @return return a list with all the travels registered
	 */
	List<Travel> readAllTravels();

	/**
	 * Read the travel that corresponds with the id joined.
	 * 
	 * @param id Integer that corresponds with a id of a travel
	 * @return If the id is registered, returns a travel object, in other case
	 *         returns null
	 */
	Travel readTravelById(Integer id);

	/**
	 * Delete the travel that corresponds with the id joined
	 * 
	 * @param id Integer that corresponds with an id of a travel
	 * @return 
	 */
	String deleteTravel(Integer id);

	/**
	 * Save a new register of a travel. occupationCity and repeatedRegister
	 * functions let verify that the occupation of the city is less than 5 and there
	 * is not a repeated register for the travel that the client is trying to enter
	 * 
	 * @param travel An object of travel type that will be created in the travel
	 *               table on the database
	 * @return return the response state for the operation requested.
	 */
	String saveTravel(Travel travel) ;

	/**
	 * Give a List of travel that corresponds with the id of the tourist joined
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a list of travels, in other case
	 *         returns null
	 */
	List<Travel> filterByTouristId(String id);

	/**
	 * Give you a List of travel that corresponds with the name of the city joined
	 * 
	 * @param id String that corresponds with a name of a city
	 * @return If the name is registered, returns a list of travels, in other case
	 *         returns null
	 */
	List<Travel> filterByCityName(String cityName);
	
	/**
	 * 
	 * @param travel
	 * @return
	 */
	boolean verifyRegister(Travel travel);

}

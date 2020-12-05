package com.turismo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.turismo.models.Travel;

/**
 * TravelRepository extends JpaRepository for have principal operations in a
 * CRUD program
 * 
 * @author Brayan Hernandez
 *
 */
@Repository
public interface TravelRepository extends JpaRepository<Travel, Integer> {

	/**
	 * Filter the table Travel using the tourist id
	 * 
	 * @param touristId String that corresponds to a id of a tourist
	 * @return If the id of the tourist is registered, return A list of travels that
	 *         contains all the travels for the id of the tourist joined, in other
	 *         case returns null
	 */
	@Query("SELECT u FROM Travel u WHERE u.idTourist = ?1")
	List<Travel> findByTouristId(String touristId);

	/**
	 * Filter the table Travel using the city name
	 * 
	 * @param cityName String that corresponds to a name of a city
	 * @return If the name of the city is registered, return a list of travels that
	 *         contains all the travels for the name of the city joined, in other
	 *         case returns null
	 */
	@Query("SELECT u FROM Travel u WHERE u.cityName LIKE %?1%")
	List<Travel> findByCity(String cityName);

	/**
	 * Query that let verify that only five tourist can be register a travel for a
	 * city on the same day
	 * 
	 * @param travelDate String that corresponds to the date for the travel that the
	 *                   client is trying to create
	 * @param cityName   String that corresponds to the destination city for the
	 *                   travel that the client is trying to create
	 * @return If there is one or more registers for the date and the city joined,
	 *         returns a list with that Travels registers, in other case returns an
	 *         empty list
	 */
	@Query("SELECT u FROM Travel u WHERE u.travelDate = ?1 AND u.cityName = ?2")
	List<Travel> occupationCity(String travelDate, String cityName);

	/**
	 * Query that let prevents a repeated register for a tourist in the same city
	 * and date
	 * 
	 * @param travelDate String that corresponds to the date for the travel that the
	 *                   client is trying to create
	 * @param cityName   String that corresponds to the destination city for the
	 *                   travel that the client is trying to create
	 * @param idTourist  String that corresponds to the id of the tourist that the
	 *                   client is trying to create
	 * @return A list of travel for the parameters joined, this list should be empty
	 *         for do not repeat a register
	 */
	@Query("SELECT u FROM Travel u WHERE u.travelDate = ?1 AND u.cityName = ?2 AND u.idTourist = ?3")
	List<Travel> repeatedRegister(String travelDate, String cityName, String idTourist);

}

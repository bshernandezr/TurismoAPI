package com.turismo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.turismo.models.City;

/**
 * Interface for city Services
 * 
 * @author Brayan Hernandez
 *
 */
@Service
public interface ICityService {

	/**
	 * Read all the cities in the table.
	 * 
	 * @return return a list with all the cities registered
	 */
	List<City> readAllCities();

	/**
	 * Read the city that corresponds with the id joined.
	 * 
	 * @param id Integer that corresponds with a id of a city
	 * @return If the id is registered, returns a city object, in other case returns
	 *         null
	 * 
	 */
	City readCityById(Integer id);

	/**
	 * Delete the city that corresponds with the id entered
	 * 
	 * @param id Integer that corresponds with an id of a city
	 * @return Response for the request
	 */
	String deleteCity(Integer id);

	/**
	 * Save a new register of a city.
	 * 
	 * @param city An object of city type that will be create in the city table on
	 *             the database+
	 * @return Response for the request
	 */
	String saveCity(City city);
}

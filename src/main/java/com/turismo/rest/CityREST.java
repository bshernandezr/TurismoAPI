package com.turismo.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turismo.models.City;
import com.turismo.services.ICityService;

/**
 * Rest controller for city entity
 * 
 * @author Brayan Hernandez
 *
 */
@RestController
@RequestMapping("/City")
public class CityREST {

	@Autowired
	public ICityService cityService;

	/**
	 * Create a new register of a city. Should be called by a POST request
	 * 
	 * @param city An object of city type that will be create in the city table on
	 *             the database
	 * @return Response for the request, "Created successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String create(@RequestBody City city) {
		if (cityService.readCityById(city.getId()) != null) {
			return "This id City is already registered";
		} else {
			return cityService.saveCity(city);
		}
	}

	/**
	 * Read all the cities in the table. Should be called by a GET request
	 * 
	 * @return return a list with all the cities registered
	 */
	@GetMapping(value = "/read", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<City> read() {
		return cityService.readAllCities();
	}

	/**
	 * 
	 * Read the city that corresponds with the id joined. Should be called by a GET
	 * request
	 * 
	 * @param id Integer that corresponds with a id of a city
	 * @return If the id is registered, returns a city object, in other case returns
	 *         null
	 */
	@GetMapping(value = "/read/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public City readById(@PathVariable("id") Integer id) {
		return cityService.readCityById(id);
	}

	/**
	 * Delete the city that corresponds with the id joined Should be called by a
	 * DELETE request
	 * 
	 * @param id Integer that corresponds with an id of a city
	 * @return Response for the request, "Deleted successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id) {
		return cityService.deleteCity(id);
	}

	/**
	 * Edit a city in the table, Should be called by a PUT request
	 * 
	 * @param city An object of city type that will be updated in the table.
	 * @return Response for the request, "Updated successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String edit(@RequestBody City city) {
		return cityService.saveCity(city);
	}
}

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
import com.turismo.models.Travel;
import com.turismo.services.ITouristService;
import com.turismo.services.ITravelService;

/**
 * Rest controller for travel entity
 * 
 * @author Brayan Hernandez
 *
 */
@RestController
@RequestMapping("Travel")
public class TravelREST {

	@Autowired
	private ITravelService travelService;
	@Autowired
	private ITouristService touristService;

	/**
	 * Create a new register of a travel. Should be called by a POST request
	 * 
	 * @param travel An object of travel type that will be created in the travel
	 *               table on the database
	 * @return Response for the request, "Created successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String create(@RequestBody Travel travel) {
		if (touristService.readTouristById(travel.getIdTourist()) == null) {
			return "Cannot save this register. This tourist id is not registered.";
		}
		if (travelService.verifyRegister(travel)) {
			return "Cannot save this register. This travel is already registered. ";
		}
		return travelService.saveTravel(travel);
	}

	/**
	 * Read all the travels in the table. Should be called by a GET request
	 * 
	 * @return return a list with all the travels registered
	 */
	@GetMapping(value = "/read", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Travel> read() {
		return travelService.readAllTravels();
	}

	/**
	 * Read the travel that corresponds with the id entered. Should be called by a
	 * GET request
	 * 
	 * @param id Integer that corresponds with a id of a travel
	 * @return If the id is registered, returns a travel object, in other case
	 *         returns null
	 */
	@GetMapping(value = "/read/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Travel readById(@PathVariable("id") Integer id) {
		return travelService.readTravelById(id);
	}

	/**
	 * Give a List of travel that corresponds with the id of the tourist entered
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a list of travels, in other case
	 *         returns null
	 */
	@GetMapping(value = "/filter/tourist/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Travel> readByTouristId(@PathVariable("id") String id) {
		return travelService.filterByTouristId(id);
	}

	/**
	 * Give you a List of travel that corresponds with the name of the city entered
	 * 
	 * @param id String that corresponds with a name of a city
	 * @return If the name is registered, returns a list of travels, in other case
	 *         returns null
	 */
	@GetMapping(value = "/filter/city/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Travel> readByCityName(@PathVariable("name") String cityName) {
		return travelService.filterByCityName(cityName);
	}

	/**
	 * Delete the travel that corresponds with the id joined Should be called by a
	 * DELETE request
	 * 
	 * @param id Integer that corresponds with an id of a travel
	 * @return Response for the request, "Deleted successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id) {
		return travelService.deleteTravel(id);
	}

	/**
	 * Edit a travel in the table, Should be called by a PUT request
	 * 
	 * @param travel An object of travel type that will be updated in the table.
	 * @return Response for the request, "Updated successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PutMapping(value = "/edit/{id}" ,  consumes = MediaType.APPLICATION_JSON_VALUE)
	public String edit( @RequestBody Travel travel , @PathVariable("id") Integer id ) {
		travel.setId(id);
		if (travelService.verifyRegister(travel)) {
			return "Cannot update this register. There are not any changes in the travel entered.";
		} else if (travelService.saveTravel(travel).equals("Created successfully")) {
			return "Updated successfully";
		} else {
			return travelService.saveTravel(travel);
		}
	}
}

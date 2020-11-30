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
import com.turismo.models.Tourist;
import com.turismo.services.ITouristService;

/**
 * Rest controller for tourist entity
 * 
 * @author Brayan Hernandez
 *
 */
@RestController
@RequestMapping("Tourist")
public class TouristREST {

	@Autowired
	private ITouristService touristService;

	/**
	 * Create a new register of a tourist. Should be called by a POST request
	 * 
	 * @param tourist An object of tourist type that will be create in the tourist
	 *                table on the database
	 * @return Response for the request, "Created successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String create(@RequestBody Tourist tourist) {
		if (touristService.readTouristById(tourist.getId()) != null) {
			return "This id tourist is already registered";
		} else {
			return touristService.saveTourist(tourist);
		}
	}

	/**
	 * Read all the tourists in the table. Should be called by a GET request
	 * 
	 * @return return a list with all the tourists registered
	 */
	@GetMapping(value = "/read", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tourist> read() {
		return touristService.readAllTourists();
	}

	/**
	 * 
	 * Read the tourist that corresponds with the id entered. Should be called by a
	 * GET request
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a tourist object, in other case
	 *         returns null
	 */
	@GetMapping(value = "/read/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Tourist readById(@PathVariable("id") String id) {
		return touristService.readTouristById(id);
	}

	/**
	 * Delete the tourist that corresponds with the id entered. Should be called by
	 * a DELETE request
	 * 
	 * @param id String that corresponds with an id of a tourist
	 * @return Response for the request, "Deleted successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {
		return touristService.deleteTourist(id);
	}

	/**
	 * Edit a tourist in the table, Should be called by a PUT request
	 * 
	 * @param tourist An object of tourist type that will be updated in the table.
	 * @return Response for the request, "Updated successfully" if the operation was
	 *         completed successfully, in other case return a message with the error
	 */
	@PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String edit(@RequestBody Tourist tourist) {
		return touristService.saveTourist(tourist);
	}
}

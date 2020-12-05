package com.turismo.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.turismo.repository.TouristRepository;
import com.turismo.models.Tourist;

/**
 * Services for the tourist table
 * 
 * @author Brayan Hernandez
 *
 */
@Service
public class TouristService implements ITouristService {

	@Autowired
	private TouristRepository touristRepository;

	/**
	 * Read all the tourists in the table.
	 * 
	 * @return return a list with all the tourists registered
	 */
	public List<Tourist> readAllTourists() {
		return touristRepository.findAll();
	}

	/**
	 * 
	 * Read the tourist that corresponds with the id entered.
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a tourist object, in other case
	 *         returns null
	 */
	public Tourist readTouristById(String id) {
		try {
			return touristRepository.findById(id).get();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Delete the tourist that corresponds with the id entered
	 * 
	 * @param id String that corresponds with an id of a tourist
	 * @return Response for the request
	 */
	public String deleteTourist(String id) {
		if (readTouristById(id) != null) {
			touristRepository.deleteById(id);			
			return "Deleted successfully";
		} else {
			return "Cannot delete, entered id is not registered";
		}
	}

	/**
	 * Save a new register of a tourist.
	 * 
	 * @param tourist An object of tourist type that will be create in the tourist
	 *                table on the database
	 * @return Response for the request
	 */
	public String saveTourist(Tourist tourist) {
		// Verify if is an edit Operation
		if (readTouristById(tourist.getId()) != null) {
			touristRepository.save(tourist);
			return "Updated successfully";
		}
		/*
		 * If the id is not registered, save the tourist and verify this register using
		 * readTouristById method.
		 */
		touristRepository.save(tourist);
		return "Created successfully";
	}

}

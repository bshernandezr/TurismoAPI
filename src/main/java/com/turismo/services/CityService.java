package com.turismo.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.turismo.repository.CityRepository;
import com.turismo.models.City;

/**
 * Services for the city table
 * 
 * @author Brayan Hernandez
 *
 */
@Service
public class CityService implements ICityService {

	@Autowired
	private CityRepository cityRepository;

	/**
	 * Read all the cities in the table.
	 * 
	 * @return return a list with all the cities registered
	 */
	public List<City> readAllCities() {
		return cityRepository.findAll();
	}

	/**
	 * Read the city that corresponds with the id entered.
	 * 
	 * @param id Integer that corresponds with a id of a city
	 * @return If the id is registered, returns a city object, in other case returns
	 *         null
	 * 
	 */
	public City readCityById(Integer id) {
		try {
			return cityRepository.findById(id).get();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Delete the city that corresponds with the id entered
	 * 
	 * @param id Integer that corresponds with an id of a city
	 * @return Response for the request
	 */
	public String deleteCity(Integer id) {
		if (readCityById(id) != null) {
			cityRepository.deleteById(id);
			return "Deleted successfully";
		} else {
			return "Cannot delete, entered id is not registered";
		}
	}

	/**
	 * Save a new register of a city.
	 * 
	 * @param city An object of city type that will be create in the city table on
	 *             the database+
	 * @return Response for the request
	 */
	public String saveCity(City city) {
		// Verify if is an edit Operation
		if (readCityById(city.getId()) != null) {
			cityRepository.save(city);
			return "Updated successfully";
		}
		/*
		 * If the id is not registered, save the city and verify this register using
		 * readCityById method.
		 */
		cityRepository.save(city);
		return "Created successfully";

	}

}

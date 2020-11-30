package com.turismo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turismo.models.Travel;
import com.turismo.repository.TravelRepository;

/**
 * Services for the travel table
 * 
 * @author Brayan Hernandez
 *
 */
@Service
public class TravelService implements ITravelService {

	@Autowired
	private TravelRepository travelRepository;

	@Autowired
	private ITouristService touristService;

	/**
	 * Read all the travels in the table.
	 * 
	 * @return return a list with all the travels registered
	 */
	public List<Travel> readAllTravels() {
		return travelRepository.findAll();
	}

	/**
	 * Read the travel that corresponds with the id entered.
	 * 
	 * @param id Integer that corresponds with a id of a travel
	 * @return If the id is registered, returns a travel object, in other case
	 *         returns null
	 */
	public Travel readTravelById(Integer id) {
		try {
			return travelRepository.findById(id).get();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Delete the travel that corresponds with the id entered
	 * 
	 * @param id Integer that corresponds with an id of a travel
	 * @return Response for the request
	 */
	public String deleteTravel(Integer id) {
		if (readTravelById(id) != null) {
			travelRepository.deleteById(id);
			return "Deleted successfully";
		} else {
			return "Cannot delete, entered id is not registered";
		}
	}

	/**
	 * Save a register of a travel. occupationCity and repeatedRegister functions
	 * let verify that the occupation of the city is less than 5 and there is not a
	 * repeated register for the travel that the client is trying to enter
	 * 
	 * @param travel An object of travel type that will be created in the travel
	 *               table on the database
	 * @return return the response state for the operation requested.
	 */
	public String saveTravel(Travel travel) {
		/*
		 * Verify that maximum occupation and gender conditions for day and city are
		 * fulfilled, return a string with the result state that corresponds with the
		 * request
		 */
		if ((verifyGender(travel)) && (verifyOccupation(travel))) {
			travelRepository.save(travel);
			return "Created successfully";
		} else {
			if (!verifyGender(travel)) {
				return "Cannot save this register. Maximum number of male gender allowed for this city was reached.";
			} else {
				return "Cannot save this register. Maximum ocuppation allowed for this city was reached.";
			}
		}
	}

	/**
	 * Give a List of travel that corresponds with the id of the tourist joined
	 * 
	 * @param id String that corresponds with a id of a tourist
	 * @return If the id is registered, returns a list of travels, in other case
	 *         returns null
	 */
	public List<Travel> filterByTouristId(String id) {
		return travelRepository.findByTouristId(id);
	}

	/**
	 * Give a List of travel that corresponds with the name of the city joined
	 * 
	 * @param id String that corresponds with a name of a city
	 * @return If the name is registered, returns a list of travels, in other case
	 *         returns null
	 */
	public List<Travel> filterByCityName(String cityName) {
		return travelRepository.findByCity(cityName);
	}

	/**
	 * For each travel registered for this day and city, verify the gender of the
	 * tourist and update the genderCounter if the gender is M(Male)
	 * 
	 * @param travel Travel object entered by the client
	 * @return If the number of tourist registered for a day and city is less than
	 *         3, including the the gender of the tourist of the travel entered,
	 *         return true, in other case return false
	 */
	public boolean verifyGender(Travel travel) {
		Integer maxGender = 3;
		Integer genderCounter = 0;
		String cityName = travel.getCityName();
		String travelDate = travel.getTravelDate();
		List<Travel> travelsForDateAndCity = travelRepository.occupationCity(travelDate, cityName);
		for (Travel travelRegister : travelsForDateAndCity) {
			if (touristService.readTouristById(travelRegister.getIdTourist()).getGender().equals("M")) {
				genderCounter++;
			}
		}
		if (touristService.readTouristById(travel.getIdTourist()).getGender().equals("M")) {
			genderCounter++;
		}
		return (genderCounter < maxGender);
	}

	/**
	 * Verify if there is a travel with the same data than travel entered by the
	 * client.
	 * 
	 * @param travel Travel object entered by the client
	 * @return if there is a register in the travel table with the same travelDate,
	 *         cityName and idTourist return true, in other case return false
	 */
	public boolean verifyRegister(Travel travel) {
		String cityName = travel.getCityName();
		String travelDate = travel.getTravelDate();
		String idTourist = travel.getIdTourist();
		Integer repeatedRegister = travelRepository.repeatedRegister(travelDate, cityName, idTourist).size();
		return (repeatedRegister != 0);
	}

	/**
	 * Verify if the client request is an update operation, if repeatedRegister != 0
	 * this travel register does not have any change, if the idTourist change, but
	 * cityName and TravelDate do not change, subtract 1 to occupationCity
	 * 
	 * @param travel
	 * @return
	 */
	public boolean verifyOccupation(Travel travel) {
		Integer maxOccupation = 5;
		String cityName = travel.getCityName();
		String travelDate = travel.getTravelDate();
		Integer occupationCity = travelRepository.occupationCity(travelDate, cityName).size();
		if ((readTravelById(travel.getId()) != null) && (readTravelById(travel.getId()).getCityName().equals(cityName))
				&& (readTravelById(travel.getId()).getTravelDate().equals(travelDate))) {
			occupationCity--;
		}
		return (occupationCity < maxOccupation);
	}

}

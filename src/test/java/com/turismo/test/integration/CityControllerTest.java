package com.turismo.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.turismo.TurismoApiApplication;
import com.turismo.models.City;
import com.turismo.scripts.RandomString;
import com.turismo.utils.TestExpectException;


@SpringBootTest(classes = TurismoApiApplication.class)
@WebAppConfiguration
class CityControllerTest {

	private static final String SELECT_WHERE = "SELECT * FROM city WHERE id = ";	
	private static final String[] KEY = { "ID", "NAME", "POPULATION", "RECOMMENDED_HOTEL", "TOURISTIC_PLACE" };
	
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private Gson gson;	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private MockMvc mockMvc;	
	private RandomString rndString= new RandomString();	
	private SecureRandom rdm= new SecureRandom();	


	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
	/**
	 * Integration test for Create City operation
	 * 
	 * @throws TestExpectException This exception occurs when the expected conditions in a test do not fulfilled
	 */
	@Test	
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	public void testCreateCity() throws TestExpectException {
		City city= new City();
		city.setId(rdm.nextInt(1000));
		city.setName(rndString.getString());
		city.setPopulation(rdm.nextInt(1000));
		city.setRecommendedHotel(rndString.getString());
		city.setTouristicPlace(rndString.getString());
		try {
			mockMvc.perform(post("/City/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(city))
					.characterEncoding("utf-8"))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Created successfully\"}"))
					.andReturn();			
			verifyCity(city, jdbcTemplate.queryForList(SELECT_WHERE + city.getId()), 0);			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	
	/**
	 * Integration test for readAllCities 
	 * 
	 * @throws TestExpectException This exception occurs when the expected conditions in a test do not fulfilled
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateCityList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	public void readAllCitiesTest() throws TestExpectException {		
		try {
			MvcResult result = mockMvc.perform(get("/City/read")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();				
			City[] citiesApi=gson.fromJson(result.getResponse().getContentAsString(), City[].class);			
			List<Map<String, Object>> citiesDB = jdbcTemplate.queryForList("SELECT * FROM city");			
			Assert.assertEquals("Read data size does not match", citiesDB.size() , citiesApi.length);
			for (int i = 0; i < citiesDB.size(); i++) {
				verifyCity(citiesApi[i], citiesDB, i);				
			}			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}		
	}
	
	
	/**
	 * Integration test for readById
	 * 
	 * @throws TestExpectException This exception occurs when the expected conditions in a test do not fulfilled
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateCityList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	public void readByIdTest() throws TestExpectException {		
		try {
			Integer id = 5;
			MvcResult result = mockMvc.perform(get("/City/read/{id}", id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();		
			verifyCity(gson.fromJson(result.getResponse().getContentAsString(), City.class)
					, jdbcTemplate.queryForList(SELECT_WHERE + id), 0);
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}			
	}
	
	/**
	 * Integration test for deleteCity
	 * 
	 * @throws TestExpectException This exception occurs when the expected conditions in a test do not fulfilled
	 */
	@Test 
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateCityList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	public void deleteCityTest() throws TestExpectException {
		try {
			Integer id = 3;
			mockMvc.perform(delete("/City/delete/{id}", id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Deleted successfully\"}"))
					.andReturn();
			List<Map<String, Object>> citiesDB = jdbcTemplate.queryForList(SELECT_WHERE + id);
			Assert.assertEquals("City was not deleted successfully", 0, citiesDB.size());		
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}			
	}
	
	/**
	 * Integration test for editCity
	 * 
	 * @throws TestExpectException This exception occurs when the expected conditions in a test do not fulfilled
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateCityList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE city" })
	public void editCityTest() throws TestExpectException {	
		Integer id = 2;
		City city = new City();
		city.setId(id);
		city.setName(rndString.getString());
		city.setPopulation(rdm.nextInt(1000));
		city.setRecommendedHotel(rndString.getString());
		city.setTouristicPlace(rndString.getString());
		try {
			mockMvc.perform(put("/City/edit")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(city))
					.characterEncoding("utf-8"))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Updated successfully\"}"))
					.andReturn();			
			verifyCity(city, jdbcTemplate.queryForList(SELECT_WHERE + id),0);
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}				
	}
	
	/**
	 * Verify if the data in the database is equal to the data that return the API
	 * 
	 * @param city   City object in the API
	 * @param cityDB City object in database
	 * @param index  Index in the List returned by the database, if its an only
	 *               element index 0 by default
	 */
	public void verifyCity(City city, List<Map<String, Object>> cityDB, Integer index) {
		Assert.assertEquals(KEY[0], 
				city.getId().toString() , cityDB.get(index).get(KEY[0]).toString());
		Assert.assertEquals(KEY[1], 
				city.getName().toString() , cityDB.get(index).get(KEY[1]).toString());
		Assert.assertEquals(KEY[2], 
				city.getPopulation().toString() , cityDB.get(index).get(KEY[2]).toString());
		Assert.assertEquals(KEY[3], 
				city.getRecommendedHotel().toString() , cityDB.get(index).get(KEY[3]).toString());
		Assert.assertEquals(KEY[4], 
				city.getTouristicPlace().toString() , cityDB.get(index).get(KEY[4]).toString());		
	}
}


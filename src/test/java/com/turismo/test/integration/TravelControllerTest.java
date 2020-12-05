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
import com.turismo.models.Travel;
import com.turismo.scripts.RandomString;
import com.turismo.utils.TestExpectException;

@SpringBootTest(classes = TurismoApiApplication.class)
@WebAppConfiguration
class TravelControllerTest {
	
	private static final String SELECT_WHERE = "SELECT * FROM travel WHERE id = ";
	private static final String SELECT_ALL = "SELECT * FROM travel";
	private static final String[] KEY = { "ID", "ID_CITY", "CITY_NAME", "ID_TOURIST", "TRAVEL_DATE" };
	private static final String READ_ERROR = "Read data size not match";
	
	private MockMvc mockMvc;
	private RandomString rndString = new RandomString();
	private SecureRandom rdm = new SecureRandom();	
	
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private Gson gson;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * Integration test for Create travel
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = { "TRUNCATE TABLE travel"},
			scripts = "../../scripts/CreateTouristList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void createTravelTest() throws TestExpectException {
		String idTourist = "2";
		Travel travel = new Travel();
		travel.setCityName(rndString.getString());
		travel.setIdCity(rdm.nextInt(1000));
		travel.setIdTourist(idTourist);
		travel.setTravelDate(rndString.getString());
		try {
			mockMvc.perform(post("/Travel/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(travel))
					.characterEncoding("utf-8"))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Created successfully\"}"))
					.andReturn();			
			List<Map<String, Object>> travelDB = jdbcTemplate
					.queryForList(SELECT_ALL);			
			Assert.assertNotNull(travelDB.get(0).get(KEY[0]).toString());
			Assert.assertEquals(KEY[1], 
					travel.getIdCity().toString(), travelDB.get(0).get(KEY[1]).toString());
			Assert.assertEquals(KEY[2], 
					travel.getCityName(), travelDB.get(0).get(KEY[2]).toString());
			Assert.assertEquals(KEY[3], 
					travel.getIdTourist(), travelDB.get(0).get(KEY[3]).toString());
			Assert.assertEquals(KEY[4], 
					travel.getTravelDate(), travelDB.get(0).get(KEY[4]).toString());
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	
	/**
	 * Integration test for read All travels
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,  
		scripts= {"../../scripts/CreateTouristList.sql" , "../../scripts/Create5TravelRegister.sql"})
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void readAllTravelsTest() throws TestExpectException {		
		try {
			MvcResult result = mockMvc.perform(get("/Travel/read")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();
			Travel[] travelsApi = gson.fromJson(result.getResponse().getContentAsString(), Travel[].class);
			List<Map<String, Object>> travelsDB = jdbcTemplate.queryForList(SELECT_ALL);
			Assert.assertEquals(READ_ERROR, travelsDB.size(), travelsApi.length);
			for (int i = 0; i < travelsDB.size(); i++) {
				verifyTravel(travelsApi[i], travelsDB, i);
			}			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Integration test for read travel by id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/Create5TravelRegister.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void readTravelByIdTest() throws TestExpectException {		
		try {
			Integer id = 2;
			MvcResult result = mockMvc.perform(get("/Travel/read/{id}" , id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();		
			verifyTravel(gson.fromJson(result.getResponse().getContentAsString(), Travel.class), 
					jdbcTemplate.queryForList(SELECT_WHERE + id), 0);						
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Integration test for delete travel by id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/Create5TravelRegister.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void deleteTravelByIdTest() throws TestExpectException {
		try {
			Integer id = 5;
			mockMvc.perform(delete("/Travel/delete/{id}" , id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Deleted successfully\"}"))
					.andReturn();
			List<Map<String, Object>> travelDB = jdbcTemplate.queryForList(SELECT_WHERE + id);
			Assert.assertEquals("Travel was not deleted successfully", 0, travelDB.size());						
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Integration test for edit travel by id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, 
			scripts = { "../../scripts/CreateTouristList.sql", "../../scripts/Create5TravelRegister.sql" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void editTravelByIdTest() throws TestExpectException {		
		try {
			Integer id = 3;
			Travel travel = new Travel();
			travel.setId(id);
			travel.setCityName("city1");
			travel.setIdCity(1);
			travel.setIdTourist("7");
			travel.setTravelDate("01/01/21");
			mockMvc.perform(put("/Travel/edit/{id}", id)
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(travel))
					.characterEncoding("utf-8"))					
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Updated successfully\"}"))
					.andReturn();						
			verifyTravel(travel, jdbcTemplate.queryForList(SELECT_WHERE + id), 0);						
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	
	/**
	 * Integration test for read Travels by city name
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/Create5TravelRegister.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void readTravelsByCityNameTest() throws TestExpectException {		
		try {
			String cityName = "city1";
			MvcResult result = mockMvc.perform(get("/Travel/filter/city/{name}", cityName)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();
			Travel[] travelsApi = gson.fromJson(result.getResponse().getContentAsString(), Travel[].class);
			List<Map<String, Object>> travelsDB = jdbcTemplate
					.queryForList("SELECT * FROM travel WHERE city_name LIKE '%" + cityName + "%'");
			Assert.assertEquals(READ_ERROR, travelsDB.size(), travelsApi.length);
			for (int i = 0 ; i < travelsDB.size(); i++) {	
				verifyTravel(travelsApi[i], travelsDB, i);	
			}			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Integration test for read travel by tourist id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/Create5TravelRegister.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void readTravelByTouristIdTest() throws TestExpectException {		
		try {
			String touristId = "1";
			MvcResult result = mockMvc.perform(get("/Travel/filter/tourist/{id}", touristId)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();
			Travel[] travelsApi = gson.fromJson(result.getResponse().getContentAsString(), Travel[].class);
			List<Map<String, Object>> travelsDB = jdbcTemplate
					.queryForList("SELECT * FROM travel WHERE id_tourist = '" + touristId + "'");
			Assert.assertEquals(READ_ERROR, travelsDB.size(), travelsApi.length);
			for (int i = 0 ; i < travelsDB.size(); i++) {	
				verifyTravel(travelsApi[i], travelsDB, i);			
			}			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Verify if the data in the database is equal to the data in the API
	 * 
	 * @param travel Travel object in API
	 * @param travelDB Travel object in database
	 * @param index    Index in the List returned by the database, if its an only
	 *                 element index 0 by default
	 */
	public void verifyTravel (Travel travel , List<Map<String, Object>> travelDB , Integer index) {
		Assert.assertEquals(KEY[0], 
				travel.getId().toString(), travelDB.get(index).get(KEY[0]).toString());
		Assert.assertEquals(KEY[1], 
				travel.getIdCity().toString(), travelDB.get(index).get(KEY[1]).toString());
		Assert.assertEquals(KEY[2], 
				travel.getCityName(), travelDB.get(index).get(KEY[2]).toString());
		Assert.assertEquals(KEY[3], 
				travel.getIdTourist(), travelDB.get(index).get(KEY[3]).toString());
		Assert.assertEquals(KEY[4], 
				travel.getTravelDate(), travelDB.get(index).get(KEY[4]).toString());		
		
	}
	
	

}

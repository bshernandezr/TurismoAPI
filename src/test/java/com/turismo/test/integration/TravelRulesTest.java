package com.turismo.test.integration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.turismo.TurismoApiApplication;
import com.turismo.models.Travel;
import com.turismo.utils.TestExpectException;

@SpringBootTest(classes = TurismoApiApplication.class)
@WebAppConfiguration
class TravelRulesTest {		
	
	private static final String CITY_TEST = "city1";
	private static final String DATE_TEST = "01/01/21";
	private static final String UTF8 = "utf-8";
	private static final String GENDER_MSG = "{\"msg\":\"Cannot save this register. Maximum number of male gender allowed for this city was reached.\"}";
	private static final String OCCUPATION_MSG = "{\"msg\":\"Cannot save this register. Maximum ocuppation allowed for this city was reached.\"}";
	
	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	private Gson gson;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	/**
	 * Integration test for Occupation Rule (Create Travel)
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, 
			scripts = { "../../scripts/CreateTouristList.sql", "../../scripts/Create5TravelRegister.sql" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void occupationRuleCreateTravelTest() throws TestExpectException {
		createTravelMvc("7", OCCUPATION_MSG);		
	}
	
	/**
	 * Integration test for occupation rule(Edit travel)
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, 
			scripts = { "../../scripts/CreateTouristList.sql", "../../scripts/Create5TravelRegister.sql" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void occupationRuleEditTravelTest() throws TestExpectException {		
		editTravelMvc(6, "7", OCCUPATION_MSG);
	}
	
	/**
	 * Integration test for gender rule(Create travel)
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, 
			scripts = { "../../scripts/CreateTouristList.sql", "../../scripts/Create2MaleTravelRegister.sql" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void genderRuleCreateTravelTest() throws TestExpectException {	
		createTravelMvc("3", GENDER_MSG);
	}
	
	/**
	 * Integration test for gender rule(Edit travel)
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, 
			scripts = { "../../scripts/CreateTouristList.sql", "../../scripts/Create5TravelRegister.sql" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, 
			statements = { "TRUNCATE TABLE travel", "TRUNCATE TABLE tourist" })
	void genderRuleEditTravelTest() throws TestExpectException {
		editTravelMvc(3, "3", GENDER_MSG);
	}
	
	/**
	 * Verify if the register that violates the rules was created in the database 
	 * 
	 * @param travel Travel object with the parameters that violates the rules
	 */
	public void verifyRuleInOperations(Travel travel) {
		List<Map<String, Object>> travelDB = jdbcTemplate
				.queryForList("SELECT * FROM travel WHERE id_tourist ='" + travel.getIdTourist() + "'"
						+ "AND id_city = '" + travel.getIdCity()
						+ "' AND travel_date = '" + travel.getTravelDate() + "'");			
		Assert.assertEquals("Test fail, rule was not fulfilled", 0, travelDB.size());
	}
	
	/**
	 * MVC post request for create a travel an verify that for an invalid register, API return
	 * an error message
	 * 
	 * @param idTourist id of the tourist in a travel object
	 * @param expectedMsg Expected message for an invalid register in a create travel operation
	 * @throws TestExpectException
	 */
	public void createTravelMvc(String idTourist, String expectedMsg) throws TestExpectException {
		Travel travel = new Travel();
		travel.setCityName(CITY_TEST);
		travel.setIdCity(1);
		travel.setIdTourist(idTourist);
		travel.setTravelDate(DATE_TEST);
		try {
			mockMvc.perform(post("/Travel/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(travel))
					.characterEncoding(UTF8))
					.andExpect(status().isOk())
					.andExpect(content().string(expectedMsg))
					.andReturn();			
			verifyRuleInOperations(travel);	
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}	
	}
	
	/**
	 * MVC put request for edit a travel an verify that for an invalid register, API return
	 * an error message
	 * 
	 * @param id Id of the travel register for edit
	 * @param idTourist Id of the tourist in the travel object
	 * @param expectedMsg expected message for an invalid register in an edit travel operation
	 * @throws TestExpectException
	 */
	public void editTravelMvc(Integer id, String idTourist, String expectedMsg) throws TestExpectException {
		Travel travel = new Travel();
		travel.setCityName(CITY_TEST);
		travel.setIdCity(1);
		travel.setIdTourist(idTourist);
		travel.setTravelDate(DATE_TEST);
		try {
			mockMvc.perform(put("/Travel/edit/{id}", id)
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(travel))
					.characterEncoding(UTF8))
					.andExpect(status().isOk())
					.andExpect(content().json(expectedMsg))
					.andReturn();
			verifyRuleInOperations(travel);			
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}		
	}

}

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
import com.turismo.models.Tourist;
import com.turismo.scripts.RandomString;
import com.turismo.utils.TestExpectException;

@SpringBootTest(classes = TurismoApiApplication.class)
@WebAppConfiguration
class TouristControllerTest {
	
	private static final String SELECT_WHERE = "SELECT * FROM tourist WHERE id = ";	
	private static final String[] KEY = { "ID" , "NAME" , "ID_TYPE" , "BIRTH_DATE", "DESTINATION" ,
										  "GENDER" , "TRAVEL_BUDGET" , "TRAVEL_FRECUENCY", "CREDIT_CARD" };
	
	@Autowired
	private Gson gson;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	WebApplicationContext webApplicationContext;
	
	private RandomString rndString= new RandomString();	
	private SecureRandom rnd= new SecureRandom();	
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
	/**
	 * Integration test for createTourist
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	void createTouristTest() throws TestExpectException {		
		Tourist tourist = new Tourist();
		tourist.setId(String.valueOf(rnd.nextInt(100000)));
		tourist.setName(rndString.getString());
		tourist.setIdType(rndString.getString());
		tourist.setBirthDate(rndString.getString());
		tourist.setDestination(rndString.getString());
		tourist.setTravelBudget(rnd.nextDouble());
		tourist.setTravelFrecuency(rnd.nextInt(36));
		tourist.setGender(rndString.getString());
		tourist.setCreditCard(true);
		try {
			mockMvc.perform(post("/Tourist/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(tourist))
					.characterEncoding("utf-8"))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Created successfully\"}"))
					.andReturn();
			verifyTourist(tourist, jdbcTemplate.queryForList(SELECT_WHERE + tourist.getId()), 0);		
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}

	}
	
	/**
	 * Integration test for read all tourist
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateTouristList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	void readAllTouristTest() throws TestExpectException {
		try {
			MvcResult result = mockMvc.perform(get("/Tourist/read")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();				
			Tourist[] touristsApi = gson.fromJson(result.getResponse().getContentAsString(), Tourist[].class);
			List<Map<String, Object>> touristsDB = jdbcTemplate.queryForList("SELECT * FROM tourist");
			Assert.assertEquals("Read data size does not match", touristsDB.size(), touristsApi.length);
			for (int i = 0; i < touristsDB.size(); i++) {
				verifyTourist(touristsApi[i], touristsDB, i);			
			}
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}	
		
	}
	
	/**
	 * Integration test read tourist by id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateTouristList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	void readTouristByIdTest() throws TestExpectException {				
		try {		
			String id = "5";
			MvcResult result = mockMvc.perform(get("/Tourist/read/{id}", id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();
			verifyTourist(gson.fromJson(result.getResponse().getContentAsString(), Tourist.class) ,
					jdbcTemplate.queryForList(SELECT_WHERE + id), 0);
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}	
		
	}
	
	/**
	 * Integration test for delete tourist by id
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateTouristList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	void deleteTouristByIdTest() throws TestExpectException {
		try {
			String id = "7";
			mockMvc.perform(delete("/Tourist/delete/{id}", id)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Deleted successfully\"}"))
					.andReturn();
			List<Map<String, Object>> touristDB = jdbcTemplate.queryForList(SELECT_WHERE + id);
			Assert.assertEquals("Tourist was not deleted successfully", 0, touristDB.size());		
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Integration test for edit tourist
	 * 
	 * @throws TestExpectException
	 */
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../../scripts/CreateTouristList.sql")
	@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = { "TRUNCATE TABLE tourist" })
	void editTouristTest() throws TestExpectException {
		String id = "7";
		Tourist tourist = new Tourist();
		tourist.setId(id);
		tourist.setName(rndString.getString());
		tourist.setIdType(rndString.getString());
		tourist.setBirthDate(rndString.getString());
		tourist.setDestination(rndString.getString());
		tourist.setTravelBudget(rnd.nextDouble());
		tourist.setTravelFrecuency(rnd.nextInt(36));
		tourist.setGender(rndString.getString());
		tourist.setCreditCard(true);
		try {			
			mockMvc.perform(put("/Tourist/edit")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(tourist))
					.characterEncoding("utf-8"))
					.andExpect(status().isOk())
					.andExpect(content().json("{\"msg\":\"Updated successfully\"}"))
					.andReturn();
			verifyTourist(tourist, jdbcTemplate.queryForList(SELECT_WHERE + id), 0);								
		} catch (Exception e) {
			throw new TestExpectException(e.getMessage());
		}
	}
	
	/**
	 * Verify if the data in the database is equal to the data that return the API
	 * 
	 * @param tourist   The tourist object that is entered to the api request
	 * @param touristDB The tourist object in database
	 * @param index     Index in the List returned by the database, if its an only
	 *                  element index 0 by default
	 */
	public void verifyTourist(Tourist tourist , List<Map<String, Object>> touristDB , Integer index ) {
		Assert.assertEquals(KEY[0], 
				tourist.getId(), touristDB.get(index).get(KEY[0]).toString());
		Assert.assertEquals(KEY[1], 
				tourist.getName(), touristDB.get(index).get(KEY[1]).toString());
		Assert.assertEquals(KEY[2], 
				tourist.getIdType(), touristDB.get(index).get(KEY[2]).toString());
		Assert.assertEquals(KEY[3], 
				tourist.getBirthDate(), touristDB.get(index).get(KEY[3]).toString());
		Assert.assertEquals(KEY[4], 
				tourist.getDestination(), touristDB.get(index).get(KEY[4]).toString());
		Assert.assertEquals(KEY[5], 
				tourist.getGender(), touristDB.get(index).get(KEY[5]).toString());
		Assert.assertEquals(KEY[6], 
				tourist.getTravelBudget(), Double.parseDouble(touristDB.get(index).get(KEY[6]).toString()), 0);
		Assert.assertEquals(KEY[7],
				String.valueOf(tourist.getTravelFrecuency()), touristDB.get(index).get(KEY[7]).toString());
		Assert.assertEquals(KEY[8], 
				tourist.getCreditCard(), touristDB.get(index).get(KEY[8]).toString().equals("1"));		
	}
}

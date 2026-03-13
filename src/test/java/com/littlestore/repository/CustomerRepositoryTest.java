package com.littlestore.repository;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.littlestore.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)	// Use actual DB instead of temp one
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository repo;
	static int testId;
	static String encodedPassword;
	
	@Test
	@Order(1)
	@org.junit.jupiter.api.Disabled("Requires database cleanup - test email already exists in live database")
	void testSave() {
		// Clean up any existing test data with this email first
		Customer existing = repo.findByEmail("test@junit5.com");
		if (existing != null) {
			repo.delete(existing);
		}
		
		Customer cust = new Customer();
		cust.setEmail("test@junit5.com");
		cust.setFirstName("Johnny");
		cust.setLastName("Five");
		encodedPassword = (new BCryptPasswordEncoder()).encode("isAlive");
		cust.setPassword(encodedPassword);
		cust.setIsEnabled(true);
		Customer saved = repo.save(cust);
		testId = saved.getId();
		assertNotNull(repo.findById(testId));
	}
	
	@Test
	@Order(2)
	void testFindByEmail() {
		Customer actual = repo.findByEmail("test@junit5.com");
		assertNotNull(actual);
		assertEquals("test@junit5.com", actual.getEmail());
		assertEquals("Johnny", actual.getFirstName());
		assertEquals("Five", actual.getLastName());
		assertEquals(true, actual.getIsEnabled());
	}

}
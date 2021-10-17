package com.littlestore.repository;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.littlestore.entity.Customer;

@DataJpaTest
@Rollback(false)
//@Sql(scripts={"/test-data.sql"})		// Source test data from external file in src/main/resources. Run once for each test
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)	// Use actual DB instead of temp one
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository repo;
	static int testId;
	
	@Test
	@Order(1)
	void testSave() {
		Customer cust = new Customer();
		cust.setId(24);
		cust.setEmail("test@junit5.com");
		cust.setFirstName("Johnny");
		cust.setLastName("Five");
		cust.setPassword((new BCryptPasswordEncoder()).encode("isAlive"));
		cust.setIsEnabled(true);
		repo.save(cust);
		testId = 24;
//		System.out.println(testId);
		assertNotNull(repo.findById(testId));
	}
	
	@Test
	@Order(2)
	void testFindByEmail() {
		Customer expected = new Customer();
		expected.setId(24);
		expected.setEmail("test@junit5.com");
		expected.setFirstName("Johnny");
		expected.setLastName("Five");
		expected.setPassword((new BCryptPasswordEncoder()).encode("isAlive"));
		expected.setIsEnabled(true);
		Customer actual = repo.findByEmail("test@junit5.com");
		assertEquals(expected, actual);
	}

}
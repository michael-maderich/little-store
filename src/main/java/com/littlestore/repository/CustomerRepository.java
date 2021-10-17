package com.littlestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.littlestore.entity.Customer;
 
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	@Query(value = "SELECT c FROM Customer c WHERE c.firstName LIKE '%' || :keyword || '%'"
            + " OR c.lastName LIKE '%' || :keyword || '%'")
    public List<Customer> findByName(@Param("keyword") String keyword);

	public Customer findByEmail(String email);
    
/*    @Query(value = "SELECT c FROM Customer c JOIN c.salesRep s ON "
    			+ "c.salesRep_id = s.id WHERE s.name LIKE '%' || :keyword || '%'"
    			+ " OR c.name LIKE '%' || :keyword || '%'"
    			+ " OR c.email LIKE '%' || :keyword || '%'"
    			+ " OR c.address LIKE '%' || :keyword || '%'")
    public List<Customer> search(@Param("keyword") String keyword);*/

/*    @Query(value = "SELECT * FROM customer c JOIN salesrep s ON "
    				+ "c.salesRep_id = s.id WHERE s.name LIKE '%?1%")
    public List<Customer> findBySalesRepName(String salesRepName);*/
}
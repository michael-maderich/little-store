package com.littlestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.littlestore.entity.GeneralData;
 
public interface GeneralDataRepository extends CrudRepository<GeneralData, Integer>, JpaSpecificationExecutor<GeneralData> {

	@Query(value = "SELECT p.generalValue FROM GeneralData p WHERE p.generalName = :generalName")
    public String getGeneralValue(@Param("generalName") String generalName);
	
	public List<GeneralData> findAll();
	
	public List<GeneralData> findByGeneralCategory(String generalCategory);
}
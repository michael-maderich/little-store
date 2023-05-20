package com.littlestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

import com.littlestore.entity.PaymentInfo;
 
public interface PaymentInfoRepository extends CrudRepository<PaymentInfo, Integer>, JpaSpecificationExecutor<PaymentInfo> {
	public List<PaymentInfo> findAll();
}
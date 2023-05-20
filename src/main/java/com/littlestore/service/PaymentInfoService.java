package com.littlestore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littlestore.entity.PaymentInfo;
import com.littlestore.repository.PaymentInfoRepository;

@Service
public class PaymentInfoService {
	
	PaymentInfoRepository repo;
	@Autowired
	public PaymentInfoService(PaymentInfoRepository repo) {
		this.repo = repo;
	}

	public List<PaymentInfo> listAll() {
		return (List<PaymentInfo>) repo.findAll();
	}
}
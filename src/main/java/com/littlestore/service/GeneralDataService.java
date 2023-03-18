package com.littlestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.littlestore.repository.GeneralDataRepository;

@Service
public class GeneralDataService {
	
	GeneralDataRepository repo;
	@Autowired
	public GeneralDataService(GeneralDataRepository repo) {
		this.repo = repo;
	}

	public String getGeneralData(String generalName) {
		return repo.getGeneralValue(generalName);
	}
}
package com.littlestore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.littlestore.entity.GeneralData;
import com.littlestore.repository.GeneralDataRepository;

@Service
public class GeneralDataService {
	
	GeneralDataRepository repo;
	public GeneralDataService(GeneralDataRepository repo) {
		this.repo = repo;
	}

	public List<GeneralData> listAll() {
		return (List<GeneralData>) repo.findAll();
	}

	public List<GeneralData> findByCategory(String generalCategory) {
		return repo.findByGeneralCategory(generalCategory);
	}

	public String getGeneralData(String generalName) {
		return repo.getGeneralValue(generalName);
	}
}
package com.littlestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.littlestore.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findById(int id);
	public Role findByName(String roleName);
}
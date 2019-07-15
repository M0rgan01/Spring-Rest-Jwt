package com.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.entities.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long>{
public Roles findRolesByRole(String role);
}

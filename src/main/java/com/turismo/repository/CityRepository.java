package com.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turismo.models.City;

/**
 * City repository extends JpaRepository for have principal operations in a CRUD
 * program
 * 
 * @author Brayan Hernandez
 *
 */
@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

}

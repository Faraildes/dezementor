package model.dao;

import java.util.List;

import model.entities.Student;

public interface StudentDao {
	
	void insert(Student obj);
	void update(Student obj);
	void deletById(Integer id);
	Student findById(Integer id);
	List<Student> findAll();	
}

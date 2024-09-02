package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.StudentDao;
import model.entities.Student;

public class StudentService {
	
	private StudentDao dao = DaoFactory.createStudentDao();
	
	public List<Student> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Student obj) {
		if (obj.getId() == null)
			dao.insert(obj);
		dao.update(obj);
	}
	public void remove(Student obj) {
		dao.deletById(obj.getId());
	}
}

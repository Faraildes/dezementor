package model.dao;

import db.DB;
import model.dao.impl.TeacherDaoJDBC;
import model.dao.impl.StudentDaoJDBC;

public class DaoFactory {

	public static StudentDao createStudentDao() {
		return new StudentDaoJDBC(DB.getConnection());
	}
	
	public static TeacherDao createTeacherDao() {
		return new TeacherDaoJDBC(DB.getConnection());
	}
}

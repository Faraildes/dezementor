package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.StudentDao;
import model.entities.Student;
import model.entities.Teacher;

public class StudentDaoJDBC implements StudentDao {
	
	private Connection conn;
	public StudentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Student obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO student "
					+ "(Name, BirthDate, Cpf, Phone, Period) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());			
			st.setDate(2, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(3, obj.getCpf());
			st.setString(4, obj.getPhone());
			st.setInt(5, obj.getPeriod());			
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected erro! No rows aafeccted.");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}	
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Student obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE student "
					+ "SET Name = ?, BirthDate = ?, Cpf = ?, Phone = ?, Period = ? "
					+ "WHERE Id = ? ");
			
			st.setString(1, obj.getName());
			st.setDate(2, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(3, obj.getCpf());
			st.setString(4, obj.getPhone());
			st.setInt(5, obj.getPeriod());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}	
		finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void deletById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM student	WHERE Id = ? ");
			st.setInt(1, id);
			
			st.executeUpdate();
		}	
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Student findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT student.*,department.Name as DepName "					
					+ "FROM seller INNER JOIN department "							
					+ "ON seller.DepartmentId = department.Id "					
					+ "WHERE seller.Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {				
				Student obj = instantiateStudent(rs);
				return obj;
			}		
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Student instantiateStudent(ResultSet rs) throws SQLException {
		Student obj = new Student();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));		
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setCpf(rs.getString("Cpf"));
		obj.setPhone(rs.getString("Phone"));
		obj.setPeriod(rs.getInt("Period"));
		return obj;
	}
		
	@Override
	public List<Student> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM Student ORDER BY Name");
			rs = st.executeQuery();

			List<Student> list = new ArrayList<>();

			while (rs.next()) {
				Student obj = new Student();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setCpf(rs.getString("Cpf"));				
				obj.setPhone(rs.getString("Phone"));
				
				obj.setBirthDate(rs.getDate("BirthDate"));
				obj.setPeriod(rs.getInt("Period"));
				list.add(obj);
			}
			
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}	
	}	
}

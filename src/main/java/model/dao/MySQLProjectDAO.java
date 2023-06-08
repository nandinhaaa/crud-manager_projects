package model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Project;
import model.ModelException;
import model.User;

public class MySQLProjectDAO implements ProjectDAO {

	@Override
	public boolean save(Project project) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sqlInsert = "INSERT INTO companies VALUES (DEFAULT, ?, ?, ?, ?, ?);";
		
		db.prepareStatement(sqlInsert);
		
		db.setString(1, project.getName());
		db.setString(2, project.getDescription());
		db.setDate(3, project.getStart() == null ? new Date() : project.getStart());
			
		if (project.getEnd() == null)
			db.setNullDate(4);
		else db.setDate(4, project.getEnd());

		db.setInt(5, project.getUser().getId());
		
		return db.executeUpdate() > 0;	
	}

	@Override
	public boolean update(Project project) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sqlUpdate = "UPDATE companies "
				+ " SET name = ?, "
				+ " description = ?, "
				+ " start = ?, "
				+ " end = ?, "
				+ " project_id = ? "
				+ " WHERE id = ?; "; 
		
		db.prepareStatement(sqlUpdate);
		
		db.setString(1, project.getName());
		db.setString(2, project.getDescription());
		
		db.setDate(3, project.getStart() == null ? new Date() : project.getStart());
		
		if (project.getEnd() == null)
			db.setNullDate(4);
		else db.setDate(4, project.getEnd());
		
		db.setInt(5, project.getUser().getId());
		db.setInt(6, project.getId());
		
		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(Project project) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sqlDelete = " DELETE FROM companies "
		         + " WHERE id = ?;";

		db.prepareStatement(sqlDelete);		
		db.setInt(1, project.getId());
		
		return db.executeUpdate() > 0;
	}

	@Override
	public List<Project> listAll() throws ModelException {
		DBHandler db = new DBHandler();
		
		List<Project> companies = new ArrayList<Project>();
			
		// Declara uma instrução SQL
		String sqlQuery = " SELECT c.id as 'project_id', c.*, u.* \n"
				+ " FROM companies c \n"
				+ " INNER JOIN users u \n"
				+ " ON c.user_id = u.id;";
		
		db.createStatement();
	
		db.executeQuery(sqlQuery);

		while (db.next()) {
			User user = new User(db.getInt("user_id"));
			user.setName(db.getString("nome"));
			user.setGender(db.getString("sexo"));
			user.setEmail(db.getString("email"));
			
			Project project = new Project(db.getInt("project_id"));
			project.setName(db.getString("name"));
			project.setDescription(db.getString("description"));
			project.setStart(db.getDate("start"));
			project.setEnd(db.getDate("end"));
		
			
			companies.add(project);
		}
		
		return companies;
	}

	@Override
	public Project findById(int id) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sql = "SELECT * FROM companies WHERE id = ?;";
		
		db.prepareStatement(sql);
		db.setInt(1, id);
		db.executeQuery();
		
		Project c = null;
		while (db.next()) {
			c = new Project(id);
			c.setName(db.getString("name"));
			c.setDescription(db.getString("description"));
			c.setStart(db.getDate("start"));
			c.setEnd(db.getDate("end"));
			
			UserDAO userDAO = DAOFactory.createDAO(UserDAO.class); 
			User user = userDAO.findById(db.getInt("user_id"));
			c.setUser(user);
			
			break;
		}
		
		return c;
	}
}

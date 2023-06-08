package model.dao;

import java.util.List;

import model.ModelException;
import model.Project;

public interface ProjectDAO {
	boolean save(Project project) throws ModelException;
	boolean update(Project project) throws ModelException;
	boolean delete(Project project) throws ModelException;
	List<Project> listAll() throws ModelException;
	Project findById(int id) throws ModelException;
}

package controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Project;
import model.ModelException;
import model.User;
import model.dao.ProjectDAO;
import model.dao.DAOFactory;

@WebServlet(urlPatterns = {"/projects", "/project/form", 
		"/project/insert", "/project/delete"})
public class ProjectsController extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String action = req.getRequestURI();
		
		switch (action) {
		case "/crud-manager/project/form": {
			CommonsController.listUsers(req);
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-project.jsp");			
			break;
		}
		case "/crud-manager/project/update": {
			
			break;
		}
		default:
			listCompanies(req);
			
			ControllerUtil.transferSessionMessagesToRequest(req);
		
			ControllerUtil.forward(req, resp, "/projects.jsp");
		}
	}
	
	private void listCompanies(HttpServletRequest req) {
		ProjectDAO dao = DAOFactory.createDAO(ProjectDAO.class);
		
		List<Project> projects = null;
		try {
			projects = dao.listAll();
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
		
		if (projects != null)
			req.setAttribute("projects", projects);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String action = req.getRequestURI();
		
		switch (action) {
		case "/crud-manager/projects/insert": {
			insertProject(req, resp);			
			break;
		}
		case "/crud-manager/project/delete" :{
			
			deleteProject(req, resp);
			
			break;
		}
		default:
			System.out.println("URL inválida " + action);
		}
		
		ControllerUtil.redirect(resp, req.getContextPath() + "/projects");
	}

	private void deleteProject(HttpServletRequest req, HttpServletResponse resp) {
		String projectIdParameter = req.getParameter("id");
		
		int projectId = Integer.parseInt(projectIdParameter);
		
		ProjectDAO dao = DAOFactory.createDAO(ProjectDAO.class);
		
		try {
			Project project = dao.findById(projectId);
			
			if (project == null)
				throw new ModelException("Projeto não encontrada para deleção.");
			
			if (dao.delete(project)) {
				ControllerUtil.sucessMessage(req, "Projeto '" + 
						project.getName() + "' deletado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Projeto '" + 
						project.getName() + "' não pode ser deletado. "
								+ "Há dados relacionados ao projeto.");
			}
		} catch (ModelException e) {
			// log no servidor
			if (e.getCause() instanceof 
					SQLIntegrityConstraintViolationException) {
				ControllerUtil.errorMessage(req, e.getMessage());
			}
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void insertProject(HttpServletRequest req, HttpServletResponse resp) {
		String projectName = req.getParameter("name");
		String description = req.getParameter("description");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		Integer userId = Integer.parseInt(req.getParameter("user"));
		
		Project proj = new Project();
		proj.setName(projectName);
		proj.setDescription(description);
		proj.setStart(ControllerUtil.formatDate(start));
		proj.setEnd(ControllerUtil.formatDate(end));
		proj.setUser(new User(userId));
		
		
		ProjectDAO dao = DAOFactory.createDAO(ProjectDAO.class);
	
		try {
			if (dao.save(proj)) {
				ControllerUtil.sucessMessage(req, "Projeto '" + proj.getName() 
				+ "' salvo com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Projeto '" + proj.getName()
				+ "' não pode ser salvo.");
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}
	
}

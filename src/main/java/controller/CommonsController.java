package controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import model.ModelException;
import model.User;
import model.dao.DAOFactory;
import model.dao.UserDAO;

public class CommonsController {
	
	public static void listUsers(HttpServletRequest req) {
	    UserDAO dao = DAOFactory.createDAO(UserDAO.class);

	    List<User> listUsers = null;
	    try {
	        listUsers = dao.listAll();
	        System.out.println("Lista de usuários recuperada: " + listUsers);
	    } catch (ModelException e) {
	        // Log no servidor
	        e.printStackTrace();
	    }

	    if (listUsers != null) {
	        req.setAttribute("users", listUsers);
	        System.out.println("Lista de usuários definida no atributo 'users' do HttpServletRequest.");
	    } else {
	        System.out.println("A lista de usuários está vazia ou nula.");
	    }
	}
}
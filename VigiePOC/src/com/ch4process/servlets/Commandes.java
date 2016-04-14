package com.ch4process.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ch4process.beans.MaxiIO;


/**
 * Servlet implementation class Selection
 */
@WebServlet("/Commandes")
public class Commandes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String VUE = "/WEB-INF/commandes.jsp";
	private static final String ATT_MODULE = "module";
	private MaxiIO moduleIO;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Commandes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		moduleIO = new MaxiIO("ch4pi1.ddns.net:4444");
		request.setAttribute(ATT_MODULE, moduleIO);
		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		moduleIO.update(request);
		request.setAttribute(ATT_MODULE, moduleIO);
		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}

}

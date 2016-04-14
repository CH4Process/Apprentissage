package com.ch4process.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ch4process.beans.ListeCapteurs;
import com.ch4process.forms.SelectionForm;

/**
 * Servlet implementation class Selection
 */
@WebServlet("/Selection")
public class Selection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String FORM = "/WEB-INF/selection.jsp";
	private static final String COURBE = "/WEB-INF/googlecharts.jsp";
	private static final String ATT_CAPTEURS = "listeCapteurs";
	private static final String ATT_FORM = "form";
	private static final String ATT_DATA = "selectionform";
	
	private static ListeCapteurs listeCapteurs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Selection() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		listeCapteurs = new ListeCapteurs();
		request.setAttribute(ATT_CAPTEURS, listeCapteurs);
		this.getServletContext().getRequestDispatcher(FORM).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		SelectionForm selectionForm = new SelectionForm(request);
		if (selectionForm.getErreur()!= "")
		{
			request.setAttribute(ATT_FORM, selectionForm);
			this.getServletContext().getRequestDispatcher(FORM).forward(request, response);
		}
		else
		{
			selectionForm.fetchDatas();
			request.setAttribute(ATT_DATA, selectionForm);
			this.getServletContext().getRequestDispatcher(COURBE).forward(request, response);
		}
	}

}

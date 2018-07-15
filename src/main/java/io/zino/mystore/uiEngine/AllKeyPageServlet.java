package io.zino.mystore.uiEngine;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import io.zino.mystore.storageEngine.StorageEngine;

public class AllKeyPageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setStatus(HttpStatus.OK_200);
		
		resp.getWriter().println("<table style=\"width:100%\">");
			resp.getWriter().println("<tr>");
				resp.getWriter().println("<th>AllKeyPageServlet</th>");
			resp.getWriter().println("</tr>");
		
		StorageEngine.getInstance().getKeys().forEach(key -> {
			try {
				resp.getWriter().println("<tr>");
				resp.getWriter().println("<td>"+key+"</td>");
			    resp.getWriter().println("<tr>");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		resp.getWriter().println("</table>");
	}
}

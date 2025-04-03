package com.intro;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/EmployeeForm")
public class EmployeeForm extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection connection;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root", "accord");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // to delete 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String userName = request.getParameter("uname");
        String password = request.getParameter("password");
        String address = request.getParameter("address");
        String contact = request.getParameter("contact");
        String submit = request.getParameter("submit");
        String deleteId = request.getParameter("deleteId");
        String updateId = request.getParameter("updateId");

        try {
            // Handle Delete Operation
            if (deleteId != null && !deleteId.isEmpty()) {
                String deleteQuery = "DELETE FROM form WHERE id = ?";
                PreparedStatement pst = connection.prepareStatement(deleteQuery);
                pst.setString(1, deleteId);
                int count = pst.executeUpdate();

                if (count > 0) {
                    pw.println("<h3 style='color: green;'>Record deleted successfully!</h3>");
                } else {
                    pw.println("<h3 style='color: red;'>Failed to delete record.</h3>");
                }
            }

            // Handle Update Operation
            if ("update".equalsIgnoreCase(submit)) {
                String updateQuery = "UPDATE form SET fname = ?, lname = ?, uname = ?, password = ?, address = ?, contact = ? WHERE id = ?";
                PreparedStatement pst = connection.prepareStatement(updateQuery);
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setString(3, userName);
                pst.setString(4, password);
                pst.setString(5, address);
                pst.setString(6, contact);
                pst.setString(7, updateId); // ID of the record to update
                int count = pst.executeUpdate();

                if (count > 0) {
                    pw.println("<h3 style='color: green;'>Record updated successfully!</h3>");
                } else {
                    pw.println("<h3 style='color: red;'>Failed to update record.</h3>");
                }
            }

            // Handle Insert Operation
            if ("submit".equalsIgnoreCase(submit)) {
                String insertQuery = "INSERT INTO form(fname, lname, uname, password, address, contact) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(insertQuery);
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setString(3, userName);
                pst.setString(4, password);
                pst.setString(5, address);
                pst.setString(6, contact);
                int count = pst.executeUpdate();

                if (count > 0) {
                    pw.println("<h3 style='color: green;'>Record inserted successfully!</h3>");
                    pw.println("<form action='EmployeeForm' method='post'>");
                    pw.println("<button type='submit' name='submit' value='show' "
                    		+ "style='background-color: #007bff; color: white; padding: "
                    		+ "10px 20px; border: none; border-radius: 5px; cursor:"
                    		+ " pointer;'>Show All Employees</button>");
                    pw.println("</form>");
                } else {
                    pw.println("<h3 style='color: red;'>Failed to insert record.</h3>");
                }
            }

            // Handle Show Operation
            if ("show".equalsIgnoreCase(submit) || deleteId != null || updateId != null) {
                String selectQuery = "SELECT * FROM form";
                PreparedStatement statement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = statement.executeQuery();

                pw.println("<html><head><link rel='stylesheet'"
                		+ " href='dashBoard.css'></head><body>");
                pw.println("<script>function confirmDelete() {return confirm"
                		+ "('Are you sure want to delete this record?'); }</script>");
                pw.println("<h2>Employee List</h2>");
                pw.println("<table border='1'><tr><th>ID</th>"
                		+ "<th>First Name</th><th>Last Name</th>"
                		+ "<th>User Name</th><th>Password</th>"
                		+ "<th>Address</th><th>Contact</th>"
                		+ "<th>Action</th></tr>");

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String fname = resultSet.getString("fname");
                    String lname = resultSet.getString("lname");
                    String uname = resultSet.getString("uname");
                    String pass = resultSet.getString("password");
                    String addr = resultSet.getString("address");
                    String cont = resultSet.getString("contact");

                    pw.println("<tr><td>" + id + "</td><td>" + fname + "</td><td>" + lname + "</td><td>" + uname + "</td><td>"
                            + pass + "</td><td>" + addr + "</td><td>" + cont + "</td><td>"
                            + "<form method='post' action='EmployeeForm' style='display:inline;'>"
                            + "<input type='hidden' name='deleteId' value='" + id + "'>"
                            + "<button type='submit' id='buttonDelete' onclick='return confirmDelete()'  style='background-color: red; color: white; border: none; cursor: pointer;'>Delete</button>"
                            + "</form>&nbsp;"
                            + "<form method='post' action='EmployeeForm' style='display:inline;'>"
                            + "<input type='hidden' name='updateId' value='" + id + "'>"
                            + "<button type='submit' id='buttonEdit' style='background-color: orange; "
                            + "color: white; border: none; cursor: pointer;'>Edit</button>"
                            + "</form></td></tr>");
                }

                pw.println("</table><br>");
                pw.println("<form action='index.html'>");
                pw.println("<button type='submit'>Add Employee</button>");
                pw.println("</form>");
                pw.println("</body></html>");

                resultSet.close();
                statement.close();
            }

            // Handle Edit Button Click
            if (updateId != null && !updateId.isEmpty()) {
                String selectQuery = "SELECT * FROM form WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(selectQuery);
                statement.setString(1, updateId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String fname = resultSet.getString("fname");
                    String lname = resultSet.getString("lname");
                    String uname = resultSet.getString("uname");
                    String pass = resultSet.getString("password");
                    String addr = resultSet.getString("address");
                    String cont = resultSet.getString("contact");

                    pw.println("<html><head><link rel='stylesheet' href='dashBoard.css'></head><body>");
                    pw.println("<h2>Edit Employee</h2>");
                    pw.println("<div class='form-container'>");
                    pw.println("<form action='EmployeeForm' method='post'>");
                    pw.println("<input type='hidden' name='updateId' value='" + id + "'>");
                    pw.println("<label>First Name:</label><input type='text' name='fname' value='" + fname + "' required><br>");
                    pw.println("<label>Last Name:</label><input type='text' name='lname' value='" + lname + "' required><br>");
                    pw.println("<label>User Name:</label><input type='text' name='uname' value='" + uname + "' required><br>");
                    pw.println("<label>Password:</label><input type='password' name='password' value='" + pass + "' required><br>");
                    pw.println("<label>Address:</label><input type='text' name='address' value='" + addr + "' required><br>");
                    pw.println("<label>Contact:</label><input type='text' name='contact' value='" + cont + "' required><br><br>");
                    pw.println("<button type='submit' name='submit' value='update' style='background-color: orange; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer;'>Update</button>");
                    pw.println("</form>");
                    pw.println("</div>");
                    pw.println("</body></html>");
                }

                resultSet.close();
                statement.close();
            }
        } catch (SQLException e) {
            pw.println("<h3 style='color: red;'>SQL Error: " + e.getMessage() + "</h3>");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




//package com.intro;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
///**
// * Servlet implementation class jj2Part1
// */
//@WebServlet("/jj2Part1")
//public class jj2Part1 extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//
//	Connection connection;
//
//	public jj2Part1() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		String firstName = request.getParameter("fname");
//		String LastName = request.getParameter("lname");
//		String userName = request.getParameter("uname");
//		String password = request.getParameter("password");
//		String address = request.getParameter("address");
//		String submit = request.getParameter("submit");
//		String contact = request.getParameter("contact");
//		// long contact = Long.parseLong(request.getParameter("cnum"));
//
//		PrintWriter pw = response.getWriter();
//
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root", "accord");
//		} catch (SQLException | ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			pw.print(e);
//		}
//
//		if ("submit".equalsIgnoreCase(submit)) {
//			try {
//
//				String query = "insert into form(fname,lname,uname,password,address,contact) values(?,?,?,?,?,?)";
//				PreparedStatement pst = connection.prepareStatement(query);
//				pst.setString(1, firstName);
//				pst.setString(2, LastName);
//				pst.setString(3, userName);
//				pst.setString(4, password);
//				pst.setString(5, address);
//				pst.setString(6, contact);
//				int count = pst.executeUpdate();
//
//				if (count > 0) {
//					pw.print("<h1>Executed successfully</h1>");
//				} else {
//					pw.print("<h1>Not executed</h1>");
//				}
//				pw.println("</table>");
//				pw.println("<form action='jj2Part1.html'>");
//				pw.println("<button type='submit'>Add Employee</button></form>");
//				pw.println("<form action='jj2Part1'>");
//				pw.println("<button type='submit'>View the employee list</button></form>");
//				pw.println("</body></html>");
//				pw.println("</body></html>");
//			} catch (SQLException e) {
//				pw.print(e);
//			}
//
//		} else if ("show".equalsIgnoreCase(submit)) {
//			try {
//				String query1 = "select * from form";
//				PreparedStatement statement = connection.prepareStatement(query1);
//				ResultSet resultSet = statement.executeQuery();
//
//				pw.println("<html><head><link rel='stylesheet' href='dashBoard.css'></head><body>");
//				pw.println("<h2>Employee List</h2><br>");
//				pw.println(
//						"<table border='1'><tr><th>First_name</th><th>Last_name</th><th>User_name</th><th>Password</th>"
//								+ "<th>Address</th><th>Contact</th><th>ID</th></tr>");
//
//				while (resultSet.next()) {
//					int id = resultSet.getInt("id");
//					String firstname = resultSet.getString("fname");
//					String lastname = resultSet.getString("lname");
//					String username = resultSet.getString("uname");
//					String pass = resultSet.getString("password");
//					String addr = resultSet.getString("address");
//					String cont = resultSet.getString("contact");
//					pw.println("<tr><td>" + firstname + "</td><td>" + lastname + "</td><td>" + username + "</td><td>"
//							+ pass + "</td><td>" + addr + "</td><td>" + cont + "</td><td>" + id + "</td></tr>");
//				}
//
//				pw.println("</table><br>");
//				pw.println("<form action='jj2Part1.html'>");
//				pw.println("<button type='submit'>Add Employee</button></form>");
//				pw.println("</body></html>");
//
//				resultSet.close();
//				statement.close();
//				connection.close();
//			} catch (SQLException e) {
//				pw.print(e);
//			}
//		} else {
//			pw.print("No operations done");
//		}
//	}
//
//}
//
//// doGet(request, response);





//package com.intro;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@WebServlet("/jj2Part1")
//public class jj2Part1 extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//    Connection connection;
//
//    public void init() throws ServletException {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/servlet", "root", "accord");
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/html");
//        PrintWriter pw = response.getWriter();
//
//        String firstName = request.getParameter("fname");
//        String lastName = request.getParameter("lname");
//        String userName = request.getParameter("uname");
//        String password = request.getParameter("password");
//        String address = request.getParameter("address");
//        String contact = request.getParameter("contact");
//        String submit = request.getParameter("submit");
//        String deleteId = request.getParameter("deleteId");
//
//        try {
//            // Handle Delete Operation
//            if (deleteId != null && !deleteId.isEmpty()) {
//                String deleteQuery = "DELETE FROM form WHERE id = ?";
//                PreparedStatement pst = connection.prepareStatement(deleteQuery);
//                pst.setString(1, deleteId);
//                int count = pst.executeUpdate();
//
//                if (count > 0) {
//                    pw.println("<h3 style='color: green;'>Record deleted successfully!</h3>");
//                } else {
//                    pw.println("<h3 style='color: red;'>Failed to delete record.</h3>");
//                }
//            }
//
//            // Handle Insert Operation
//            if ("submit".equalsIgnoreCase(submit)) {
//                String insertQuery = "INSERT INTO form(fname, lname, uname, password, address, contact) VALUES (?, ?, ?, ?, ?, ?)";
//                PreparedStatement pst = connection.prepareStatement(insertQuery);
//                pst.setString(1, firstName);
//                pst.setString(2, lastName);
//                pst.setString(3, userName);
//                pst.setString(4, password);
//                pst.setString(5, address);
//                pst.setString(6, contact);
//                int count = pst.executeUpdate();
//
//                if (count > 0) {
//                    pw.println("<h3 style='color: green;'>Record inserted successfully!</h3>");
//                    // Add a button to show the table
//                    pw.println("<form action='jj2Part1' method='post'>");
//                    pw.println("<button type='submit' name='submit' value='show' style='background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer;'>Show All Employees</button>");
//                    pw.println("</form>");
//                }else{
//
//                pw.println("<h3 style='color:red;'>falied to insert record</h3>");
//                }
//            }
//
//            // Handle Show Operation
//            if ("show".equalsIgnoreCase(submit) || deleteId != null) {
//                String selectQuery = "SELECT * FROM form";
//                PreparedStatement statement = connection.prepareStatement(selectQuery);
//                ResultSet resultSet = statement.executeQuery();
//
//                pw.println("<html><head><link rel='stylesheet' href='dashBoard.css'></head><body>");
//                pw.println("<h2>Employee List</h2>");
//                pw.println("<table border='1'><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>User Name</th><th>Password</th><th>Address</th><th>Contact</th><th>Action</th></tr>");
//
//                while (resultSet.next()) {
//                    String id = resultSet.getString("id");
//                    String fname = resultSet.getString("fname");
//                    String lname = resultSet.getString("lname");
//                    String uname = resultSet.getString("uname");
//                    String pass = resultSet.getString("password");
//                    String addr = resultSet.getString("address");
//                    String cont = resultSet.getString("contact");
//
//                    pw.println("<tr><td>" + id + "</td><td>" + fname + "</td><td>" + lname + "</td><td>" + uname + "</td><td>"
//                            + pass + "</td><td>" + addr + "</td><td>" + cont + "</td><td>"
//                            + "<form method='post' action='jj2Part1' style='display:inline;'>"
//                            + "<input type='hidden' name='deleteId' value='" + id + "'>"
//                            + "<button type='submit' style='background-color: red; color: white; border: none; cursor: pointer;'>X</button>"
//                            + "</form></td></tr>");
//                }
//
//                pw.println("</table><br>");
//                pw.println("<form action='jj2Part1.html'>");
//                pw.println("<button type='submit'>Add Employee</button>");
//                pw.println("</form>");
//                pw.println("</body></html>");
//
//                resultSet.close();
//                statement.close();
//            }
//        } catch (SQLException e) {
//            pw.println("<h3 style='color: red;'>SQL Error: " + e.getMessage() + "</h3>");
//        }
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        doPost(request, response); // Redirect GET requests to POST for simplicity
//    }
//
//    public void destroy() {
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}

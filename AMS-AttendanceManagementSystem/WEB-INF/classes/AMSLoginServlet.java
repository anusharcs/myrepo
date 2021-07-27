import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.util.Date;


public class AMSLoginServlet extends HttpServlet  
{

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/AMS";
    
    static final String USER = "root";
    static final String PASS = "aarthi00*";


  public void doGet(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException  
  {

		HttpSession session = request.getSession();
		Date createTime = new Date(session.getCreationTime());
		String userIDKey = new String("username");
		String userID = request.getParameter("username");
		String pwd = request.getParameter("password");
		PrintWriter out = res.getWriter();
		String user_type="";
		int flag=0;
		 // Check if this is new comer on your web page.
 
		 Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs=null;
 
		try {
			 Class.forName(JDBC_DRIVER);
			 conn = DriverManager.getConnection(DB_URL, USER, PASS);
             String query="{call proc_login(?,?,?)}";
             CallableStatement cstmt=conn.prepareCall(query);
             cstmt.setString(1,userID);
             cstmt.setString(2,pwd);
             cstmt.registerOutParameter(3,Types.VARCHAR);
             cstmt.executeUpdate();
             user_type=cstmt.getString(3);
			 cstmt.close();
		 
		if(user_type.equals("student"))
		 {
				RequestDispatcher rd = request.getRequestDispatcher("./StudentHomePage.html");
                session.setAttribute(userIDKey, userID);
				stmt=conn.prepareStatement("select name from student where student_id=?");
				stmt.setString(1,userID);
				rs=stmt.executeQuery();
				while(rs.next())
					session.setAttribute("Name", rs.getString("name"));
				rd.forward(request, res);
		 }
		 
		else if(user_type.equals("faculty"))
		 {
				RequestDispatcher rd = request.getRequestDispatcher("./FacultyHomePage.html");
                session.setAttribute(userIDKey, userID);
				stmt=conn.prepareStatement("select name from faculty where faculty_id=?");
				stmt.setString(1,userID);
				rs=stmt.executeQuery();
				while(rs.next())
					session.setAttribute("Name", rs.getString("name"));
				rd.forward(request, res);
		 }
		 
		else if(user_type.equals("admin"))
		 {
				RequestDispatcher rd = request.getRequestDispatcher("./adminExport.html");
                session.setAttribute(userIDKey, userID);
				rd.forward(request, res);
		 }

         else{
            out.println("Sorry UserName or Password Error!");  
            RequestDispatcher rd=request.getRequestDispatcher("./index.html");  
            rd.include(request, res);
         }
		 rs.close();
		stmt.close();
		conn.close();
		}
		catch(SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			} catch(Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			} finally {
				//finally block used to close resources
				try {
					if(stmt != null)
					stmt.close();
				}
				catch(SQLException se2) {
				} // nothing we can do
				try {
				if(conn!=null)
				conn.close();
				} 
				catch(SQLException se) {
				se.printStackTrace();
			   } //end finally try
			}
  }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
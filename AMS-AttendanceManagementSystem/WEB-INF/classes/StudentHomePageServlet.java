import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;

public class StudentHomePageServlet extends HttpServlet  
{

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/AMS";
    static final String USER = "root";
    static final String PASS = "aarthi00*";


  public void doGet(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException  
  {

        HttpSession session = request.getSession(false);
        Connection conn=null;
        PreparedStatement stmt=null;
        CallableStatement cstmt=null;
        String code="<h1>Hello,"+(String)session.getAttribute("Name")+"</h1>";;
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            out.println(code);
            String sid=(String)session.getAttribute("username");
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                String query="{call proc_student_course_list(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,sid);
                ResultSet rs = cstmt.executeQuery(); 
                String thead="<table><tr><th>Enrolled CID</th><th>Course Name</th><th>Percentage</th></tr>";
				out.println(thead);
                while(rs.next()){
                    String acid=rs.getString("acid");
                    code="<tr><td class='courseref'><a href='StudentAttendance?acid="+acid+"'>"+acid+"</a></td>"+"<td>"+rs.getString("description")+"</td>"+"<td>"+rs.getFloat("percentage")+"</td></tr>";
					out.println(code);
                }
                rs.close();
                cstmt.close();
                conn.close();
            }
            catch(SQLException se){
                se.printStackTrace();
            }
            catch(Exception e){
            e.printStackTrace();
            }
            finally{
                try{
                    if(stmt!=null)
                        stmt.close();
                }
                catch(SQLException se2){
                }
                try{
                    if(conn!=null)
                        conn.close();
                }
                catch(SQLException se){
                    se.printStackTrace();
                }
            }
            
            
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
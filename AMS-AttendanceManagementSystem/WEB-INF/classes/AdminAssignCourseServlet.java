import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.time.*;


public class AdminAssignCourseServlet extends HttpServlet  
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
        String code="";
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                String acid=request.getParameter("acid");
                String course_id=request.getParameter("course_id");
                String faculty_id=request.getParameter("facultyid");
                stmt=conn.prepareStatement("INSERT INTO active_course VALUES (?,?,?);");
                stmt.setString(1,acid);
				stmt.setString(2,course_id);
                stmt.setString(3,faculty_id);
                stmt.executeUpdate();
                String sid=request.getParameter("student_id");
                String[] sid_list=sid.split(",");
                float initial_percentage=0;
                for(int i=0;i<sid_list.length;i++){
                    stmt=conn.prepareStatement("insert into enrollment values (?,?,?)");
                    stmt.setString(1,acid);
                    stmt.setString(2,sid_list[i]);
                    stmt.setFloat(3,initial_percentage);
                    stmt.executeUpdate(); 
                }
                
           
                stmt.close();
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
            RequestDispatcher rd = request.getRequestDispatcher("./adminExport.html");
			rd.forward(request, res);
            
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
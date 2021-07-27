import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;

public class FacultyTakeAttendance extends HttpServlet  
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
        String code1="";
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            String fid=(String)session.getAttribute("username");
            String acid=(String)request.getParameter("acid");
            code="<form method='POST' action='./SubmitAttendance'><input type='hidden' name='acid' value='"+acid+"'><label id='takelabel' for='timestamp'>Date and Time of Class</label><input type='datetime-local' required id='timestamp' name='timestamp'>";
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt=conn.prepareStatement("select course.description from course,active_course where course.course_id=active_course.course_id && active_course.acid=?");
                stmt.setString(1,acid);
                ResultSet rs=stmt.executeQuery();
                String coursename="";
                while(rs.next()){
                    coursename=rs.getString("description");
                }
                code1="<h2>"+acid+"-"+coursename+"</h2><h2>Enter Attendance</h2>";
                String query="{call proc_get_students(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,acid);
                rs = cstmt.executeQuery(); 
                String thead="<table><tr> <th>Register Number</th><th>Student Name</th><th>Attendance(Mark if absent)</th></tr>";
                code=code+thead;
                while(rs.next()){
                    String stu_id=rs.getString("student_id");
                    String stu_name=rs.getString("name");
                    code=code +"<tr> <td>"+stu_id+"</td>"+"<td>"+stu_name+"</td><td>"+"<input type='checkbox' name='"+stu_id+"'>"+"</td><tr>";
                }
                code=code+"</table><input type='submit' id='takeenter' value='Enter'></form>";
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
            out.print(docType +
            "<html>\n" +
                "<head><title>" + "Faculty" + "</title>\n" +
                "<LINK REL='stylesheet' href='./style.css' type='text/css'> "+
                "</head>\n"+
                "<body>\n" +
                    code1+
                    code+
                "</body>"+
            "</html>");
            
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
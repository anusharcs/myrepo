import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.time.*;


public class GetActiveCourseIDS extends HttpServlet  
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
                stmt=conn.prepareStatement("select acid from active_course;");
                ResultSet rs=stmt.executeQuery();
                while(rs.next()){
                    String cid=rs.getString("acid");
                    out.print("<option value='"+cid+"'>"+cid+"</option>");
                }
                rs.close();
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
            
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
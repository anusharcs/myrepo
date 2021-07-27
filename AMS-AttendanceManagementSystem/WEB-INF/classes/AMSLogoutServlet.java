import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;

public class AMSLogoutServlet extends HttpServlet    
{

  static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/AMS";
  static final String USER = "root";
  static final String PASS = "aarthi00*";

  public void doGet(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException  
  {
    CallableStatement cstmt=null;
    Connection conn=null;
    res.setContentType("text/html") ;
    PrintWriter out = res.getWriter();
    HttpSession session = request.getSession(true);                        
    String id=(String)session.getAttribute("username");
    try{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        cstmt=conn.prepareCall("call proc_remove_notifications(?)");
        cstmt.setString(1,id);
        cstmt.executeUpdate();
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
              if(cstmt!=null)
                  cstmt.close();
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
    session.invalidate();
    RequestDispatcher rd=request.getRequestDispatcher("./index.html");  
    rd.forward(request, res);
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}

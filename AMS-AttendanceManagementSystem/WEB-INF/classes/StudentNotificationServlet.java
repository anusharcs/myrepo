import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.time.*;


public class StudentNotificationServlet extends HttpServlet  
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
            String sid=(String)session.getAttribute("username");
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                String query="{call proc_get_notifications(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,sid);
				String ulhead="<ul>";
                ResultSet rs = cstmt.executeQuery();
                ArrayList<String> acids=new ArrayList<String>();
                while(rs.next()){
                    String acid=rs.getString("acid");
                    String type=rs.getString("type");
					if((type.equals("percentage")) && !acids.contains(acid)){
						ulhead=ulhead+"<li>Your attendance in "+acid+" has fallen below the cut-off.</li>";
                        acids.add(acid);
                    }
					else if(type.equals("absence"))
						ulhead=ulhead+"<li>Your were marked absent in "+acid+" for the session "+rs.getInt("session_id")+".</li>";
                }
				ulhead=ulhead+"</ul>";
				out.println(ulhead);
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
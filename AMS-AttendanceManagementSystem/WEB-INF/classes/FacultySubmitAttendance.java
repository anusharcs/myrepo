import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class FacultySubmitAttendance extends HttpServlet  
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
        String query;
        ArrayList<String> ids = new ArrayList<String>();
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            String fid=(String)session.getAttribute("username");
            String acid=(String)request.getParameter("acid");
            String text="";
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                query="{call proc_get_students(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,acid);
                ResultSet rs = cstmt.executeQuery(); 
                while(rs.next()){
                    String stu_id=rs.getString("student_id");
                    ids.add(stu_id);
                }
                query="{call proc_new_session(?,?,?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,acid);
                text = request.getParameter("timestamp");
                text=text.replace("T"," ");
                text=text + ":00";
                Timestamp ts = Timestamp.valueOf(text);
                cstmt.setTimestamp(2,ts);
                cstmt.registerOutParameter(3,Types.INTEGER);
                cstmt.executeUpdate();
                int sess_id=cstmt.getInt(3);
                for(int i=0;i<ids.size();i++){
                    if(request.getParameter(ids.get(i))!=null){
                        query="{call proc_add_absentee(?,?,?)}";
                        cstmt=conn.prepareCall(query);
                        cstmt.setString(1,ids.get(i));
                        cstmt.setString(2,acid);
                        cstmt.setInt(3,sess_id);
                        cstmt.executeUpdate();
                    }
                }
                query="{call proc_acid_percentage(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,acid);
                cstmt.executeUpdate();
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
            RequestDispatcher rd = request.getRequestDispatcher("./FacultyHomePage.html");
			rd.forward(request, res);
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse res) throws ServletException,  IOException
    {
        doGet(request,res);
    }

}
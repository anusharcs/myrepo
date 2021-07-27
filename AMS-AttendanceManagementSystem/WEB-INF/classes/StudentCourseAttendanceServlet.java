import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.time.*;


public class StudentCourseAttendanceServlet extends HttpServlet  
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
        String table="";
		int total_sessions=0;
		int total_absent=0;
		String temp="";
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            out.println(docType +
            "<html>\n" +
                "<head>" +
                "<LINK REL='stylesheet' href='./style.css' type='text/css'> "+
                "</head>\n"+
                "<body>\n");
            String sid=(String)session.getAttribute("username");
			String acid=request.getParameter("acid"); 
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt=conn.prepareStatement("select course.description from course,active_course where course.course_id=active_course.course_id && active_course.acid=?");
                stmt.setString(1,acid);
                ResultSet  rs=stmt.executeQuery();
                String coursename="";
                while(rs.next()){
                    coursename=rs.getString("description");
                }
                out.print("<h2>"+acid+"-"+coursename+"</h2>");
                String query="{call proc_student_attendance(?,?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,sid);
				cstmt.setString(2,acid);
                rs = cstmt.executeQuery(); 
                String thead="<table><tr><th>Session ID</th><th>Time Stamp</th><th>Present/Absent</th></tr>";
				table=thead;
                while(rs.next()){
					total_sessions=total_sessions+1;
					if(rs.getInt("absent")==1)
					{
						total_absent=total_absent+1;
						temp="Absent";
					}
					else
						temp="Present";
					java.sql.Timestamp ts= rs.getTimestamp("timestamp");
                    table=table+"<tr><td>"+rs.getInt("session_id")+"</td>"+"<td>"+ts.toString()+"</td>"+"<td>"+temp+"</td></tr>";
                }
				code="";
				code="<p>Total classes: "+total_sessions+"</p>";
				code=code+"<p>Total absent: "+total_absent+"</p>";
                int total_present=total_sessions-total_absent;
				float percentage=((float)total_present/(float)total_sessions)*100;
				code=code+"<p>Attendance Percentage: "+percentage+"</p>";
				float next_percentage=((float)total_present/((float)total_sessions+1))*100;
				code=code+"<p>Attendance Percentage If You Miss Next Class: "+next_percentage+"</p>";
				int can_miss=0;
				int need_present=0;
				float calc=0;
				int temp_sessions=total_sessions;
				if(percentage<75.0)
				{
					code=code+"<p>Classes that you can afford to miss: "+can_miss+"</p>";
                    need_present=3*total_sessions-4*total_present;
                    need_present=need_present+1;
					code=code+"<p>Classes you need to attend to stay above cut-off: "+need_present+"</p>";
					
				}
				else
				{
					can_miss=(-3*total_sessions+4*total_present)/3;
                    can_miss=can_miss+1;
					code=code+"<p>Classes that you can afford to miss: "+can_miss+"</p>";
					
						
				}
				out.print(table + code);
				out.print("</body></html>");
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
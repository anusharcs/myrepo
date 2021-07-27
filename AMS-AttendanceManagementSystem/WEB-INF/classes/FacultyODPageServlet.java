import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;

public class FacultyODPageServlet extends HttpServlet  
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
        String code="ods";
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            String fid=(String)session.getAttribute("username");
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                code="<table> <tr> <th>Student ID</th> <th>Active Course ID</th> <th>Session ID</th> <th>Justification</th> <th>Proof</th> <th>Status</th></tr>";
                String query="{call proc_get_faculty_od(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,fid);
                ResultSet rs = cstmt.executeQuery(); 
                String app="";
                String done="";
                while(rs.next()){
                    int sess_id=rs.getInt("session_id");
                    String acid=rs.getString("acid");
                    String stu_id=rs.getString("student_id");
                    String just=rs.getString("justification");
                    String prf=rs.getString("proof");
                    String stat=rs.getString("status");
                    if(stat.equals("applied")){
                        String id=stu_id+";"+acid+";"+sess_id;
                        app = app + "<form id='" + id +"'> <tr id='" + id+"table"+"'> <td><input type='text' name='stuid' value='" + stu_id +"' readonly></td>" + "<td><input type='text' name='acid' value='" + acid +"' readonly></td>" + "<td><input type='text' name=''sessid' value='" + sess_id +"' readonly></td>" + "<td><input type='text' name='just' value='" + just +"' readonly></td>" + "<td><input type='text' name='proof' value='" + prf +"' readonly></td>" +"<td><input type='button' name='approve' value='Approve' id='"+ id+"approved"+ "'onclick=\"odprocess('approved','"+ id+"')\"> <input type='button' value='Deny' name='deny' id='"+ id+"denied"+"'onclick=\"odprocess('denied','"+ id+"')\"></td></tr></form>";
                    }
                    else{
                        done=done +"<tr><td>"+stu_id+"</td>"+"<td>"+acid+"</td>"+"<td>"+sess_id+"</td><td>"+just+"</td><td>"+prf+"</td><td>"+stat+"</td></tr>";
                    }
                }
                code=code+app+done+"</table>";
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
                "<style> table, th, td {border: 1px solid black;} table{width:80%;margin-top:20px;margin-bottom:20px;}</style>"+
                "</head>\n"+
                "<body>\n" +
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
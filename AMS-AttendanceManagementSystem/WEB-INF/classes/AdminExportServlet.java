import java.io.*;                
import javax.servlet.*;
import javax.servlet.http.*;    
import java.util.*;
import java.sql.*;
import java.text.*;
import java.util.Date;

public class AdminExportServlet extends HttpServlet  
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
        ResultSet rs;
        String code="";
        String code1="";
        String query;
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Float> percentages = new ArrayList<Float>();
        ArrayList<Integer> sess_ids = new ArrayList<Integer>();
        if(session == null){
            res.sendRedirect("./index.html");
        }
        else{
            res.setContentType("text/html") ;
            PrintWriter out = res.getWriter();
            String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
            String acid=(String)request.getParameter("acid"); 
            int tot_classes=0;
            float avg_percentage=0;;
            int nob75=0;
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt=conn.prepareStatement("select course.description from course,active_course where course.course_id=active_course.course_id && active_course.acid=?");
                stmt.setString(1,acid);
                rs=stmt.executeQuery();
                String coursename="";
                while(rs.next()){
                    coursename=rs.getString("description");
                }
                code1="<h2>"+acid+"-"+coursename+"</h2>";
                query="{call proc_get_students(?)}";
                cstmt=conn.prepareCall(query);
                cstmt.setString(1,acid);
                rs = cstmt.executeQuery(); 
                String thead="<table><tr><th>Register Number</th><th>Name</th><th>Percentage</th>";
                while(rs.next()){
                    String id=rs.getString("student_id");
                    String name=rs.getString("name");
                    Float p=rs.getFloat("percentage");
                    ids.add(id);
                    names.add(name);
                    percentages.add(p);
                }
                stmt=conn.prepareStatement("select * from sessions where acid=? order by session_id desc");
                stmt.setString(1,acid);
                rs=stmt.executeQuery();
                String timestamps="<tr><th></th><th></th><th></th>";
                while(rs.next()){
                    int sess=rs.getInt("session_id");
                    sess_ids.add(sess);
                    java.sql.Timestamp ts= rs.getTimestamp("timestamp");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
                    String valueFromDB = ts.toString();
                    Date d1 = sdf1.parse(valueFromDB);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
                    String dateWithoutTime = sdf.format(d1);
                    thead=thead+"<th>"+sess+"</th>";
                    timestamps=timestamps+"<th>"+dateWithoutTime+"</th>";
                    tot_classes++;
                }
                thead=thead+"</tr>"+timestamps+"</tr>";
                code=code+thead;
                for(int i=0;i<ids.size();i++){
                    avg_percentage+=percentages.get(i);
                    if(percentages.get(i)<75){
                        nob75++;
                    }
                    String trow="<tr>"+"<td>"+ids.get(i)+"</td>"+"<td>"+names.get(i)+"</td>"+"<td>"+percentages.get(i)+"</td>";
                    query="{call proc_student_attendance(?,?)}";
                    cstmt=conn.prepareCall(query);
                    cstmt.setString(1,ids.get(i));
                    cstmt.setString(2,acid);
                    rs = cstmt.executeQuery(); 
                    while(rs.next()){
                        int isabsent=rs.getInt("absent");
                        String a;
                        if(isabsent==1){
                            a="Absent";
                        }
                        else{
                            a="Present";
                        }
                        trow = trow  +"<td>"+a+"</td>";                 
                    } 
                    code=code+trow+"</tr>";
                }
                code=code+"<tr><td>Absentees</td><td></td><td></td>";
                avg_percentage=avg_percentage/ids.size();
                for(int i=0;i<sess_ids.size();i++){
                    String abslis="<td><ul>";
                    stmt=conn.prepareStatement("select name from absentee_list,student where acid=? && session_id=? && student.student_id=absentee_list.student_id;");
                    stmt.setString(1,acid);
                    stmt.setInt(2,sess_ids.get(i));
                    rs=stmt.executeQuery();
                    while(rs.next()){
                        abslis=abslis+"<li>"+rs.getString("name")+";</li>";
                    }
                    abslis=abslis+"</ul></td>";
                    code=code+abslis;
                }
                code=code+"</tr></table>";
                out.print(code);
                rs.close();
                cstmt.close();
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
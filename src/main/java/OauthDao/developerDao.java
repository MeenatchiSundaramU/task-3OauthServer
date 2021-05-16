package OauthDao;
import java.sql.*;

import model.developerModel;

public class developerDao 
{
     public static Connection connect() throws ClassNotFoundException, SQLException 
     {
    	 Class.forName("org.sqlite.JDBC");
    	 Connection con=DriverManager.getConnection("jdbc:sqlite:C://sqlite-tools-win32-x86-3350500//oauth.db");
    	 return con;
     }
     public static void InsertUser(developerModel devuser) throws ClassNotFoundException, SQLException
     {
    	 System.out.print("Entered Databse");
    	 Connection conn=connect();
    	 PreparedStatement st=conn.prepareStatement("insert into developerdb(clientid,clientsecret,appname,redirecturi) values(?,?,?,?)");
    	 st.setString(1,devuser.getClientid());
    	 st.setString(2,devuser.getClientSecret());
    	 st.setString(3,devuser.getAppname());
    	 st.setString(4,devuser.getRedirecturi());
    	 st.executeUpdate();
          st.close();
          conn.close();
    	 System.out.print("Databse Closed");
     }
     public static boolean verifyDeveloper(String clientid,String redirecturi) throws ClassNotFoundException, SQLException
     {
    	 Connection conn=connect();
    	 PreparedStatement st=conn.prepareStatement("select * from developerdb where clientid=? and redirecturi=?");
    	 st.setString(1, clientid);
    	 st.setString(2, redirecturi);
    	 ResultSet rs=st.executeQuery();
    	 if(rs.next()==true)
    	 {
    		 st.close();
    		 conn.close();
    		 return true;
    	 }
    	 else
    	 {
    		 st.close();
    		 conn.close();
    		 return false;
    	 }
     }
}

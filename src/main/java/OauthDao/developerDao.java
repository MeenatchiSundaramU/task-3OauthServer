package OauthDao;
import java.sql.*;
import java.util.ArrayList;
import model.DeveloperModel;


public class developerDao 
{
	//InsertQuery into developer console's redirect uri 
	static String redirecturi_1="insert into devredirecturi_1(clientuid,redirecturi_1)values(?,?)";
	static String redirecturi_2="insert into devredirecturi_2(clientuid,redirecturi_2)values(?,?)";
	static String redirecturi_3="insert into devredirecturi_3(clientuid,redirecturi_3)values(?,?)";
	
	//Query to check the valid redirect uri among the uri's
	
	static String validuri_1="select * from devredirecturi_1 where redirecturi_1=? and clientuid=?";
	static String validuri_2="select * from devredirecturi_2 where redirecturi_2=? and clientuid=?";
	static String validuri_3="select * from devredirecturi_3 where redirecturi_3=? and clientuid=?";
	
	//Connection with Databases
     public static Connection connect() throws ClassNotFoundException, SQLException 
     {
    	 Class.forName("org.sqlite.JDBC");
    	 Connection con=DriverManager.getConnection("jdbc:sqlite:C://sqlite-tools-win32-x86-3350500//msserver.db");
    	 return con;
     }
     
     //Insert the developer data to developerdb table
     public static void InsertUser(DeveloperModel users,int total_redirect_uri) throws ClassNotFoundException, SQLException
     {
    	 System.out.print("Entered Databse");
    	 int clientuid; 
		  ArrayList<String> redurisQuery = new ArrayList<String>();
		  ArrayList<String> reduri=new ArrayList<String>();
		  redurisQuery.add(redirecturi_1);
		  redurisQuery.add(redirecturi_2);
		  redurisQuery.add(redirecturi_3);
		  reduri.add(users.getRedirecturi_1());
		  reduri.add(users.getRedirecturi_2());
		  reduri.add(users.getRedirecturi_3());
  	      Connection conn=connect();
  	      PreparedStatement st=conn.prepareStatement("insert into developerdb(clientid,clientsecret,appname) values(?,?,?)");
  	      st.setString(1,users.getClientid());
  	      st.setString(2,users.getClientSecret());
  	      st.setString(3,users.getAppname());
  	      st.executeUpdate();
   	      Statement stm=conn.createStatement();
   	      ResultSet rst=stm.executeQuery("select max(clientuid) as UID from developerdb");
   	      clientuid=rst.getInt("UID");
   	      System.out.print(clientuid);
   	      stm.close();
   	      rst.close();
   	      //Loop to add the redirecturi's to respective tables based on total_redirect_uri
   	      for(int i=0;i<total_redirect_uri;i++)
   	      {
   	    	st=conn.prepareStatement(redurisQuery.get(i));
   	    	st.setInt(1,clientuid);
   	    	st.setString(2,reduri.get(i));
   	    	st.executeUpdate();
   	      }
  	      st.close();
          conn.close();
    	  System.out.print("Databse Closed");
     }
     
     //Verify the client id and redirecturi
     public static boolean verifyDeveloper(String clientid,String redirecturi) throws ClassNotFoundException, SQLException
     {
    	 
    	 ArrayList<String> checkredirecturis = new ArrayList<String>();
    	 checkredirecturis.add(validuri_1);
    	 checkredirecturis.add(validuri_2);
    	 checkredirecturis.add(validuri_3);
    	 int flag=0;
    	 Connection conn=connect();
    	 PreparedStatement st=conn.prepareStatement("select * from developerdb where clientid=?");
    	 st.setString(1, clientid);
    	 ResultSet rs=st.executeQuery();
    	 if(rs.next()==true)
    	 {
    		 int clientuid=rs.getInt("clientuid");
    		 for(int i=0;i<3;i++)
    		 {
    			 st=conn.prepareStatement(checkredirecturis.get(i));
    			 st.setString(1,redirecturi);
    			 st.setInt(2, clientuid);
    			 rs=st.executeQuery();
    			 if(rs.next()==true)
    			 {
    				 flag=1;
    				 break;
    			 }
    		 }
    		 if(flag!=0)
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
    	 else
    	 {
    		 st.close();
    		 conn.close();
    		 return false;
    	 }
     }
}

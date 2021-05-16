package OauthDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import model.CreateAccModel;
import java.text.ParseException;
public class CreateAccDao 
{
	//Create an account and saved the usersdata into database
	public static void InsertUser(CreateAccModel creuser) throws ClassNotFoundException, SQLException
    {
   	 System.out.print("Entered Databse");
   	 Connection conn=developerDao.connect();
   	 PreparedStatement st=conn.prepareStatement("insert into userinfo(name,email,phone,password,location) values(?,?,?,?,?)");
   	 st.setString(1,creuser.getName());
   	 st.setString(2,creuser.getEmail());
   	 st.setString(3,creuser.getPhone());
   	 st.setString(4,creuser.getPassword());
   	 st.setString(5, creuser.getLocation());
   	 st.executeUpdate();
     java.sql.Statement stm=(java.sql.Statement)conn.createStatement();
     ResultSet rst=stm.executeQuery("select max(uid) as UID from userinfo");
     int uids=rst.getInt("UID");
     //Insert uids into Scope to table. 
     st=conn.prepareStatement("insert into scopetable(uid,profile,location) values(?,?,?)");
     st.setInt(1, uids);
	 st.setString(2,"1");
	 st.setString(3,"1");
	 st.executeUpdate();
	 st.close();
	 conn.close();
   	 System.out.print("Databse Closed");
    }
	
	//Check the users credentials when logging the accounts
	public static int checkUser(String email,String pass) throws ClassNotFoundException, SQLException
	{
		 System.out.print("Entered Databse");
		Connection conn=developerDao.connect();
		PreparedStatement st=conn.prepareStatement("select * from userinfo where email=? and password=?");
		st.setString(1, email);
		st.setString(2, pass);
		ResultSet rs=st.executeQuery();
		if(rs.next()==false)
		{
			  st.close();
			  conn.close();
		     return 0;
		}
		else
		{
			System.out.print(rs.getInt("uid"));
		    int uids=rs.getInt("uid");
		    st.close();
		    conn.close();
		    return uids;
		}
	}
	
	
	//Check the whether the resource owner have access to scope which mentioned in the url
	public static boolean checkScope(int uids,String scopename) throws SQLException, ClassNotFoundException
	{
		System.out.print("Entered Databse"+scopename+uids);
		Connection conn=developerDao.connect();
		PreparedStatement st;
		if(scopename=="profile")
		st=conn.prepareStatement("select * from scopetable where uid=? and profile=\"1\"");
		else
		st=conn.prepareStatement("select * from scopetable where uid=? and location=\"1\"");
		st.setInt(1,uids);
		ResultSet rs=st.executeQuery();
		if(rs.next()==false)
		{
			st.close();
			conn.close();
		     return false;
		}
		else
		{
			st.close();
			conn.close();
			return true;
		}
	}
	
	
	//Stored the Authorization grant code 
	public static void StoreGrandCode(String clientid,int uids,String time,String grandcode) throws SQLException, ClassNotFoundException
	{
		 Connection conn=developerDao.connect();
	   	 PreparedStatement st=conn.prepareStatement("insert into grandcodelog(clientid,uid,grantcode,timestamp) values(?,?,?,?)");
	   	 st.setString(1,clientid);
	   	 st.setInt(2,uids);
	   	 st.setString(3,grandcode);
	   	 st.setString(4,time);
	   	 st.executeUpdate();
	   	 st.close();
	   	 conn.close();
	}
	
	
	//Validation the grant code
	public static boolean validateGrandCode(String grantcode) throws SQLException, ClassNotFoundException, ParseException
	{
		 Connection conn=developerDao.connect();
	   	 PreparedStatement st=conn.prepareStatement("select * from grandcodelog where grantcode=?");
	   	 st.setString(1,grantcode);
	   	 ResultSet rs=st.executeQuery();
	   	 if(rs.next()==false)
	   		  return false;
	   	 else {
	   	 String tokgtime=rs.getString("timestamp");
	   	 Calendar cal = Calendar.getInstance();
	   	 SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	   	 cal.setTime((sdf.parse(tokgtime)));
	   	 System.out.print(cal.getTime().toString());
	   	 Calendar cal2= Calendar.getInstance();
	   	 st=conn.prepareStatement("delete from grandcodelog where uid=? and grantcode=?");
	   	 st.setInt(1, rs.getInt("uid"));
	   	 st.setString(2, grantcode);
	   	 st.executeUpdate();
	   	 if(cal.compareTo(cal2)>0)
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
	
	//Save the access tokens and refresh tokens
	public static void SaveTokens(String accesstoken,String refreshtoken,String accesstime,int uid,String clientid) throws ClassNotFoundException, SQLException
	{
		Connection conn=developerDao.connect();
		PreparedStatement checktok=conn.prepareStatement("select * from accesstoken where uid=? and clientid=? and accesstoken=?");
		checktok.setInt(1, uid);
		checktok.setString(2, clientid);
		checktok.setString(3, accesstoken);
		ResultSet rscheck=checktok.executeQuery();
		if(rscheck.next()!=false)
		{
			checktok=conn.prepareStatement("delete from accesstoken where accesstoken=?");
			checktok.setString(1,rscheck.getString("accesstoken"));
			checktok.executeUpdate();
			checktok.close();
			PreparedStatement accessst=conn.prepareStatement("insert into accesstoken(clientid,uid,accesstoken,timestamp)values(?,?,?,?)");
		   	accessst.setString(1,clientid);
			accessst.setInt(2,uid);
		   	accessst.setString(3,accesstoken);
		   	accessst.setString(4,accesstime);
		   	accessst.executeUpdate();
			accessst.close();
			conn.close();
		}
		else
		{
			PreparedStatement accessst=conn.prepareStatement("insert into accesstoken(clientid,uid,accesstoken,timestamp)values(?,?,?,?)");
		   	accessst.setString(1, clientid);
			accessst.setInt(2,uid);
		   	accessst.setString(3,accesstoken);
		   	accessst.setString(4,accesstime);
		   	accessst.executeUpdate();
			accessst.close();
		PreparedStatement refreshst=conn.prepareStatement("insert into refreshtoken(clientid,uid,refreshtoken,tokenremain)values(?,?,?,?)");
	   	refreshst.setString(1, clientid);
		refreshst.setInt(2,uid);
	   	refreshst.setString(3,refreshtoken);
	   	refreshst.setInt(4,20);
	   	refreshst.executeUpdate();
	   	refreshst.close();
	   	conn.close();
		}
	}
	
	//Validate the access tokens
	public static boolean ValidateAccessToken(String accesstoken,int uid) throws ClassNotFoundException, SQLException, ParseException
	{
		Connection conn=developerDao.connect();
		PreparedStatement checktok=conn.prepareStatement("select * from accesstoken where accesstoken=? and uid=?");
		checktok.setString(1, accesstoken);
		checktok.setInt(2, uid);
		ResultSet rscheck=checktok.executeQuery();
		if(rscheck.next()==false)
			return false;
		else
		{
			String actime=rscheck.getString("timestamp");
			 Calendar cal = Calendar.getInstance();
		   	 SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		   	 cal.setTime((sdf.parse(actime)));
		   	 System.out.print(cal.getTime().toString());
		   	 Calendar cal2= Calendar.getInstance();
		   	 if(cal.compareTo(cal2)>0)
		   	 {
		   		checktok.close();
				conn.close();
		   	      return true;
		   	 }
		   	 else
		   	 {
		   		checktok.close();
				conn.close();
		   		return false;
		   	 }
		}
	}
	
	//Get the userinfo resources
	public static CreateAccModel getUsers(int uid) throws SQLException, ClassNotFoundException
	{
		Connection conn=developerDao.connect();
		PreparedStatement checktok=conn.prepareStatement("select * from userinfo where uid=?");
		checktok.setInt(1, uid);
		ResultSet rscheck=checktok.executeQuery();
		rscheck.next();
		CreateAccModel users=new CreateAccModel();
		users.setName(rscheck.getString("name"));
		users.setEmail(rscheck.getString("email"));
		users.setPhone(rscheck.getString("phone"));
		return users;
	}
	
	//Revoke the access token
	public static boolean DeleteToken(String accesstoken) throws ClassNotFoundException, SQLException
	{
		Connection conn=developerDao.connect();
		PreparedStatement checktok=conn.prepareStatement("delete from accesstoken where accesstoken=?");
		checktok.setString(1, accesstoken);
		checktok.executeUpdate();
		conn.close();
	    return true;
	}
	
}

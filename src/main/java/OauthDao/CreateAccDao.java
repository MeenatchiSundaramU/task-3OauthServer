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
		if(scopename.equals("profile")==true)
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
	
	//Acknowledged to Server Database the resource owner grants permission to that particular resources
	public static void acknowledgeResourceGranted(String clientid,int uid,String scopename) throws SQLException, ClassNotFoundException
	{
		Connection conn=developerDao.connect();
		PreparedStatement st;
		if(scopename.equals("profile")==true)
		st=conn.prepareStatement("insert into resourcegranted(clientid,uid,profile,location)values(?,?,1,0)");
		else
		st=conn.prepareStatement("insert into resourcegranted(clientid,uid,profile,location)values(?,?,0,1)");
		st.setString(1,clientid);
		st.setInt(2,uid);
		st.executeUpdate();
		st.close();
		conn.close();
	}
	
	//Stored the Authorization grant code 
	public static void StoreGrandCode(String clientid,int uids,String time,String grandcode,String scopename) throws SQLException, ClassNotFoundException
	{
		 Connection conn=developerDao.connect();
	   	 PreparedStatement st=conn.prepareStatement("insert into grantcodelog(clientid,uid,grantcode,timestamp) values(?,?,?,?)");
	   	 st.setString(1,clientid);
	   	 st.setInt(2,uids);
	   	 st.setString(3,grandcode);
	   	 st.setString(4,time);
	   	 st.executeUpdate();
	   	 st.close();
	   	 conn.close();
	}
	
	
	//Validation the grant code for generation of access token
	public static boolean validateGrandCode(String grantcode) throws SQLException, ClassNotFoundException, ParseException
	{
		 Connection conn=developerDao.connect();
	   	 PreparedStatement st=conn.prepareStatement("select * from grantcodelog where grantcode=?");
	   	 st.setString(1,grantcode);
	   	 ResultSet rs=st.executeQuery();
	   	 if(rs.next()==false)
	   		  return false;
	   	 else {
	   	 String grandtoktime=rs.getString("timestamp");
	   	 Calendar tokcal = Calendar.getInstance();
	   	 SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	   	 tokcal.setTime((sdf.parse(grandtoktime)));
	   	 Calendar currtime= Calendar.getInstance();
	   	 st=conn.prepareStatement("delete from grantcodelog where uid=? and grantcode=?");
	   	 st.setInt(1, rs.getInt("uid"));
	   	 st.setString(2, grantcode);
	   	 st.executeUpdate();
	   	 if(tokcal.compareTo(currtime)>0)
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
	
	//Save access token
	public static void saveAccessTokens(String clientid,int uid,String accesstoken,String timestamp) throws SQLException, ClassNotFoundException
	{
		System.out.println("Access token database enter");
		Connection conn=developerDao.connect();
		PreparedStatement savetok=conn.prepareStatement("insert into accesstoken(clientid,uid,accesstoken,timestamp)values(?,?,?,?)");
		savetok.setString(1, clientid);
		savetok.setInt(2, uid);
		savetok.setString(3, accesstoken);
		savetok.setString(4, timestamp);
		savetok.executeUpdate();
		savetok.close();
		conn.close();
		System.out.println("Access token database closed");
	}
	
	//Process the refresh Token below
	public static String saveRefreshToken(String clientid,int uid,String accesstoken,String timestamp) throws ClassNotFoundException, SQLException
	{
		saveAccessTokens(clientid, uid, accesstoken, timestamp);
		int tok_consumes=0;
		String refreshTokens;
		Connection conn=developerDao.connect();
		PreparedStatement checkRefAvail=conn.prepareStatement("select max(tokenremain) as REMAIN from refreshtoken where clientid=? and uid=?");
		checkRefAvail.setString(1, clientid);
		checkRefAvail.setInt(2, uid);
		ResultSet tokconsumes=checkRefAvail.executeQuery();
		if(tokconsumes.next()==false)
		{
			refreshTokens=generateRefreshToken(1, conn);
			saveRefreshTokens(clientid, uid, refreshTokens,1, conn);
		}
		else
		{
			tok_consumes=(((tokconsumes.getInt("REMAIN"))%20)+1);
			refreshTokens=generateRefreshToken(tok_consumes,conn);
			saveRefreshTokens(clientid, uid, refreshTokens,tok_consumes, conn);
		}
		conn.close();
	    return refreshTokens;
	}
	
	//Validate the access tokens for API call
	public static boolean ValidateAccessToken(String accesstoken,int uid,String scope) throws ClassNotFoundException, SQLException, ParseException
	{
		Connection conn=developerDao.connect();
		if(checkResourcePermission(uid,scope,conn)) // To check whether this client have permission to access the protected resources on behalf of user
		{
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
		else
		{
			return false;
		}
	}
	
	//Get the userinfo resources with help of access token
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
	
	//Revoke the Refresh Token
	public static boolean DeleteToken(String refreshtoken,int uid,String clientid) throws ClassNotFoundException, SQLException
	{
		Connection conn=developerDao.connect();
		PreparedStatement checktok=conn.prepareStatement("delete from refreshtoken where refreshtoken=? and uid=? and clientid=?");
		checktok.setString(1, refreshtoken);
		checktok.setInt(2, uid);
		checktok.setString(3, clientid);
		int checkdel=checktok.executeUpdate();
		conn.close();
		if(checkdel==0)
			return false;
		else
	        return true;
	}
	
	//Generate Refresh Tokens
	public static String generateRefreshToken(int tokcnt,Connection conn) throws SQLException
	{
		
		PreparedStatement getRefreshTok=conn.prepareStatement("select * from refreshtokencollections where tokenid=?");
		getRefreshTok.setInt(1, tokcnt);
		ResultSet refTok=getRefreshTok.executeQuery();
		refTok.next();
		return refTok.getString("tokens");
	}
	
	//Save Refresh Tokens after generating,with respect to clientid,uid
	public static void saveRefreshTokens(String clientid,int uid,String refreshTokens,int tokcnt,Connection conn) throws SQLException
	{
		PreparedStatement saveReftok=conn.prepareStatement("insert into refreshtoken(clientid,uid,refreshtoken,tokenremain)values(?,?,?,?)");
		saveReftok.setString(1, clientid);
		saveReftok.setInt(2, uid);
		saveReftok.setString(3, refreshTokens);
		saveReftok.setInt(4, tokcnt);
		saveReftok.executeUpdate();
		saveReftok.close();
	}
	
	//When client made an API call to access protected resources,First verified whether the client have permission to access the resources 
	public static boolean checkResourcePermission(int uid,String scope,Connection conn) throws SQLException
	{
		
		PreparedStatement checkper=conn.prepareStatement("select * from resourcegranted where uid=? and "+scope+"=1");
		checkper.setInt(1, uid);
		ResultSet rs=checkper.executeQuery();
		if(rs.next()==false)
			return false;
		else
			return true;
	}
	
	//Validate refreshToken
	public static boolean ValidateRefToken(String refreshtoken,int uid,String clientid) throws SQLException, ClassNotFoundException
	{
		Connection conn=developerDao.connect();
		PreparedStatement validTok=conn.prepareStatement("select * from refreshtoken where refreshtoken=? and uid=? and clientid=?");
	    validTok.setString(1, refreshtoken);
	    validTok.setInt(2, uid);
	    validTok.setString(3, clientid);
	    ResultSet rs=validTok.executeQuery();
	    if(rs.next()==true)
	    	return true;
	    else
	    	return false;
	}
}

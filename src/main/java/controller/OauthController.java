package controller;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import OauthDao.CreateAccDao;
import OauthDao.developerDao;
import model.CreateAccModel;

@WebServlet(value="/")
public class OauthController extends HttpServlet 
{
           protected void service(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException            {
	            String path=req.getServletPath();
	            switch(path)
	            {
	            
	            //When user clicks on sign in with mano this case will called
	            case "/Sign":              sign(req,resp);
	                                        break;
	            
	            //When client gets registered in developer console this case will called
	            case "/devdb":              try {
						                    devDetails(req,resp);
						                    } catch (ClassNotFoundException | IOException | SQLException e1) {
						                    e1.printStackTrace();}
	                                        break;
	                                        
	            //When user create an account on mano server this case will called
	            case "/createdetail" :      try {
						                    createDetails(req,resp);} catch (ClassNotFoundException | SQLException e) {
						                    e.printStackTrace();}
	                                        break;
	            
	            //When user logs in their mano's accounts on server this case will called
	            case "/logdetail" :         try {
						                    LogVerified(req,resp);
					                        } catch (ClassNotFoundException | SQLException | IOException e) {
						                    e.printStackTrace();}
	                                        break;
	                                        
	            //After authorization from resource owner, grantcode should sent to client,For that this case called
	            case "/grantcodesent":      try {
						                    grantCode(req,resp);} catch (ClassNotFoundException | SQLException | IOException e) {
						                    e.printStackTrace();}
	                                        break;
	            
	            //If the resource owner denied the authorization this case will called
	            case "/errorcode"   :       deniedGrantResponse(req,resp);
	                                        break;
	            
	            //By exchange the authorization code for getting the access token this case called
	            case "/accesstoken" :       try {
						                    AccessTokenGeneration(req,resp);
					                        } catch (ClassNotFoundException | SQLException | ParseException | IOException e){e.printStackTrace();}
	                                        break;
	                                        
	             //For all Redirect uri's response,this case called
	            case "/response":           RedirectUriResp(req,resp);
	                                        break;
	                                       
	              //Generate Access Token using refresh token call,this case called
	            
	            case "/generateaccesstoken": try {
						                     genAccessUsingRef(req,resp);
					                         } catch (ClassNotFoundException | SQLException | IOException e1) {
						                     e1.printStackTrace();}
	                                         break;
	              
	              //To get userinfo resources this case will gets called
	            case "/userinfo" :           try {
						                     getUserProfileDetails(req,resp);
					                         } catch (NumberFormatException | ClassNotFoundException | SQLException | ParseException e) {
						                     e.printStackTrace();}
	                                         break;
	                                         
	             //To revoke the refresh token in which we no need longer accessing of data,This case called
	            case "/revoke" :             try {
						                     RevokeToken(req,resp);} catch (ClassNotFoundException | SQLException | IOException e) {
						                     e.printStackTrace();}
	                                         break;
	            }
            }
           
           
           //Sign in the url details fetching
           void sign(HttpServletRequest sreq,HttpServletResponse sresp) throws IOException
           {
        	 HttpSession session=sreq.getSession();
          	 session.setAttribute("clientid", sreq.getParameter("clientid"));
          	 session.setAttribute("scope",sreq.getParameter("scope"));
          	 session.setAttribute("redirecturi",sreq.getParameter("redirecturi"));
          	 session.setAttribute("accesstype",sreq.getParameter("accesstype"));
          	 sresp.sendRedirect("ManoLogin.jsp");
           }
           
           
           
           //Store developer console details to database
           void devDetails(HttpServletRequest req,HttpServletResponse resp) throws IOException, ClassNotFoundException, SQLException
           {
        	System.out.print("Inside devloper");
       	    int total_redirect_uri=1; 
       	    model.DeveloperModel newusers=new model.DeveloperModel();
       	   resp.getWriter().print(req.getParameter("appname"));
       	    newusers.setClientid(randomStringGenerator());
       	    newusers.setClientSecret(randomStringGenerator());
       	    newusers.setAppname(req.getParameter("appname"));
       	    newusers.setRedirecturi_1(req.getParameter("url1"));
       	    System.out.print(req.getParameter("url1")+req.getParameter("url2")+req.getParameter("url3"));
       	    if((req.getParameter("url2").contains("null"))==false)
       	    {
       	    	total_redirect_uri++;
       	    	newusers.setRedirecturi_2(req.getParameter("url2"));
       	    }
       	    if((req.getParameter("url3").contains("null"))==false)
       	    {
       	    	total_redirect_uri++;
       	    	newusers.setRedirecturi_3(req.getParameter("url3"));
       	    }
       	    developerDao.InsertUser(newusers,total_redirect_uri);
            }
           
           //Create an account upload to database
           void createDetails(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   CreateAccModel user=new CreateAccModel();
        	   user.setName(req.getParameter("crename"));
        	   user.setEmail(req.getParameter("cremail"));
        	   user.setPassword(req.getParameter("crepass"));
        	   user.setLocation(req.getParameter("creloc"));
        	   user.setPhone(req.getParameter("cremobile"));
        	   CreateAccDao.InsertUser(user);
        	   resp.sendRedirect("ManoLogin.jsp");
           }
           
           //Login verifying
           void LogVerified(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   int uids=CreateAccDao.checkUser(req.getParameter("logmail"),req.getParameter("logpass"));
        	    if(uids!=0)
        		   {
        		       //Check for verified Client ID and Redirect URI
        		   
        		       HttpSession session=req.getSession();
        		       session.setAttribute("uids", uids);
        		       String clientid=(String) session.getAttribute("clientid");
        		       String redirecturi=(String) session.getAttribute("redirecturi");
        		       String scope=(String) session.getAttribute("scope");
        		       String scopename=scope.substring(0,scope.indexOf("."));
        		       String scopeoperation=scope.substring(scope.indexOf(".")+1,scope.length());
        		       if(developerDao.verifyDeveloper(clientid,redirecturi)==true)
        		       {
        		    	   //Then verified the scope whether the resource owner have the resources on the server
        		    	   if(CreateAccDao.checkScope(uids,scopename))
        		    	   {
        		    		   //It is used to print in JSP which tells the resource owner which scope does client requests
        		    		   session.setAttribute("scopename", scopename);
        		    		   resp.sendRedirect("ResourceConfirm.jsp");
        		    	   }
        		    	   else
        		    		   resp.getWriter().print("You are not allowed to access the scope");
        		       }
        		       else
        		       {
        		    	   resp.getWriter().print("Check Client Id and redirect Uri");
        		       }
        		   }
        	   else
        	   {
        		   resp.sendRedirect("ManoLogin.jsp");
        	   }
           }
           
           //Create the grand code to the client
           void grantCode(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   HttpSession session=req.getSession();
        	   int uid=(int) session.getAttribute("uids");
        	   String clientid=(String) session.getAttribute("clientid");
        	   String redirecturi=(String) session.getAttribute("redirecturi");
        	   String scopename=(String)session.getAttribute("scopename");
        	   
        	   //Acknowledged to Server Database the resource owner grants permission to that particular resources
        	   System.out.print(scopename);
        	   CreateAccDao.acknowledgeResourceGranted(clientid,uid,scopename);
        	   String time=timeGenerator(2);
        	   String grandcode=randomStringGenerator();
        	   
        	   //Store the grand code Along with the timestamp and scopename
        	   CreateAccDao.StoreGrandCode(clientid,uid, time, grandcode,(String)session.getAttribute("scopename"));
        	   resp.sendRedirect(redirecturi+"?"+"code="+grandcode);
           }
           
           
           //If the resource owner denied the request for access protected resource return as error to Client
           void deniedGrantResponse(HttpServletRequest req,HttpServletResponse resp) throws IOException
           {
        	   HttpSession session=req.getSession();
        	   String redirecturi=(String) session.getAttribute("redirecturi");
        	   resp.sendRedirect(redirecturi+"?"+"code="+"error");
           }
           
           
           //Generate Access Token to the client
           //if accesstype=="online" return only access token
           //if accesstype=="offline" return refresh token along with access token'
           
           void AccessTokenGeneration(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, ParseException, IOException, ServletException
           {
        	   System.out.print("Access token entered");
        	   if(CreateAccDao.validateGrandCode(req.getParameter("code"))==true)
        	   {
        		  HttpSession session=req.getSession();
        		  String accesstype=(String) session.getAttribute("accesstype");
        		  String accesstoken=randomStringGenerator();
        		  String accesstime=timeGenerator(60);
        		  int uid=Integer.parseInt(req.getParameter("uid"));
        		  String clientid=(String)req.getParameter("clientid");
        		  if((accesstype.equals("online"))==true)
        		  {
        		  CreateAccDao.saveAccessTokens(clientid,uid,accesstoken,accesstime);
        		  session.setAttribute("refresh_token", "null");
        		  }
        		  else if(accesstype.equals("offline"))
        		  {
        		  String refreshToken=CreateAccDao.saveRefreshToken(clientid,uid,accesstoken,accesstime);
        		  session.setAttribute("refresh_token", refreshToken);
        		  }
        		  session.setAttribute("access_token", accesstoken);
        		  resp.sendRedirect(req.getParameter("redirecturi"));
        	   }
        	   else
        	   {
        		   resp.getWriter().print("{status:Invalid Code}");
        	   }
           }
           
           //RedirectUri response
           void RedirectUriResp(HttpServletRequest req,HttpServletResponse resp) throws IOException
           {
        	   if(req.getParameter("code")==null)
        	   {
        		   System.out.print("Entered into access token prints");
        		   HttpSession session=req.getSession();
        		   if(session.getAttribute("refresh_token").equals("null")==false)
        		   resp.getWriter().printf("{access_token:%s,refresh_token:%s,tokentype:%s,expires_in_sec:%d}",session.getAttribute("access_token"),session.getAttribute("refresh_token"),"Bearer",3600);
        		   else
        			   resp.getWriter().printf("{access_token:%s,tokentype:%s,expires_in_sec:%d}",session.getAttribute("access_token"),"Bearer",3600);  
        	   }
        	   else
        	   {
        		   resp.getWriter().print("Called for Code");
        	   }
           }
           
           // Get user profile details(READ SCOPE)
           
          void getUserProfileDetails(HttpServletRequest req,HttpServletResponse resp) throws NumberFormatException, SQLException, ParseException, ClassNotFoundException, IOException
           {
        	   if(CreateAccDao.ValidateAccessToken(req.getParameter("accesstoken"),Integer.parseInt(req.getParameter("uid")),req.getParameter("scope"))==true)
        	   {
        		   CreateAccModel usersinfo=CreateAccDao.getUsers(Integer.parseInt(req.getParameter("uid")));
        		   resp.getWriter().printf("{name:%s,email:%s,phone:%s}",usersinfo.getName(),usersinfo.getEmail(),usersinfo.getPhone());
        	   }
        	   else
        		   resp.getWriter().print("InValid tokens");
           }
           
          
           //Revoke the Refresh Token when no longer need to access the data
           
           void RevokeToken(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   if(CreateAccDao.DeleteToken(req.getParameter("refreshtoken"),Integer.parseInt(req.getParameter("uid")),req.getParameter("clientid"))==true)
        		   resp.getWriter().print("{success:true}");
        	   else
        		   resp.getWriter().print("{success:false}");
           }
           
           
           //We can Generate Access Token using Refresh Token
           void genAccessUsingRef(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   String refreshToken=req.getParameter("refreshtoken");
        	   int uid=Integer.parseInt(req.getParameter("uid"));
        	   String clientid=req.getParameter("clientid");
        	   String redirecturi=req.getParameter("redirecturi");
        	   if(CreateAccDao.ValidateRefToken(refreshToken,uid,clientid)==true)
        	   {  
        		  String accesstoken=randomStringGenerator();
     		      String accesstime=timeGenerator(60);
     		      CreateAccDao.saveAccessTokens(clientid,uid,accesstoken,accesstime);
     		      HttpSession session=req.getSession();
     		      session.setAttribute("access_token", accesstoken);
     		      session.setAttribute("refresh_token", "null");
     		      resp.sendRedirect(redirecturi);
        	   }
        	   else
        	   {
        		   resp.getWriter().print("Invalid Refresh Token");
        	   }
        	   
           }
           
           //Random String Generator for tokens and client secret and id
           public static String randomStringGenerator()
           {
          	    int lLimit = 97; 
          	    int rLimit = 122; 
          	    int targetStringLength =10;
          	    Random random = new Random();
                  String generatedString = random.ints(lLimit, rLimit + 1)
          	      .limit(targetStringLength)
          	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          	      .toString();
                  return "mano."+generatedString;
           }
           
           
           //Extract Time used for validate the token
           public static String timeGenerator(int timeincrease) 
           {
        		      Calendar cal = Calendar.getInstance();
        		      cal.add(Calendar.MINUTE, timeincrease);
        		      System.out.println("Updated Date = " + cal.getTime());
        		      return cal.getTime().toString();
           }
}

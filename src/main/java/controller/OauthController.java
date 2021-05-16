package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OauthDao.CreateAccDao;
import OauthDao.developerDao;
import model.CreateAccModel;
import model.developerModel;

@WebServlet(value="/")
public class OauthController extends HttpServlet 
{
           protected void service(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException            {
	            String path=req.getServletPath();
	            switch(path)
	            {
	            case "/Sign": sign(req,resp);
	                          break;
	            case "/devdb":devDetails(req,resp);
	                          break;
	            case "/createdetail" :try {
						createDetails(req,resp);} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();}
	                                  break;
	            case "/logdetail" : try {
						LogVerified(req,resp);
					    } catch (ClassNotFoundException | SQLException | IOException e) {
						e.printStackTrace();}
	                    break;
	            case "/grantcodesent":try {
						grantCode(req,resp);} catch (ClassNotFoundException | SQLException | IOException e) {
						e.printStackTrace();}
	                                  break;
	            case "/accesstoken" :try {
						AccessTokenGeneration(req,resp);
					} catch (ClassNotFoundException | SQLException | ParseException | IOException e){e.printStackTrace();}
	                                 break;
	            case "/response": RedirectUriResp(req,resp);
	                              break;
	            case "/userinfo" :try {
						getUserProfileDetails(req,resp);
					} catch (NumberFormatException | ClassNotFoundException | SQLException | ParseException e) {
						e.printStackTrace();
					}
	                              break;
	            case "/revoke" : try {
						RevokeToken(req,resp);} catch (ClassNotFoundException | SQLException | IOException e) {
						e.printStackTrace();
					}
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
          	 sresp.sendRedirect("ManoLogin.jsp");
           }
           
           
           
           //Store developer console details to database
           void devDetails(HttpServletRequest dreq,HttpServletResponse dresp) throws IOException
           {
        	   developerModel devnew=new developerModel();
          	 devnew.setAppname(dreq.getParameter("appname"));
          	 devnew.setRedirecturi(dreq.getParameter("urls"));
          	 devnew.setClientid(randomStringGenerator());
          	 devnew.setClientSecret(randomStringGenerator());
          	 try {
  				developerDao.InsertUser(devnew);
  				dresp.sendRedirect("mains.jsp");
  			} catch (ClassNotFoundException | SQLException e) {
  				e.printStackTrace();
  			}
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
        		    	   //Then verified the scope whether the resource owner have the permisson on the server
        		    	   if(CreateAccDao.checkScope(uids,scopename))
        		    		   resp.sendRedirect("ResourceConfirm.jsp");
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
        	   String time=timeGenerator(2);
        	   String grandcode=randomStringGenerator();
        	   HttpSession session=req.getSession();
        	   int uid=(int) session.getAttribute("uids");
        	   String clientid=(String) session.getAttribute("clientid");
        	   String redirecturi=(String) session.getAttribute("redirecturi");
        	   //Store the grand code Along with the timestamp
        	   CreateAccDao.StoreGrandCode(clientid,uid, time, grandcode);
        	   resp.sendRedirect(redirecturi+"?"+"code="+grandcode);
           }
           
           //Generate Access Token to the client
           void AccessTokenGeneration(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, ParseException, IOException, ServletException
           {
        	   System.out.print("Access token entered");
        	   if(CreateAccDao.validateGrandCode(req.getParameter("code"))==true)
        	   {
        		  HttpSession session=req.getSession();
        		  String accesstoken=randomStringGenerator();
        		  String refreshtoken=randomStringGenerator();
        		  String accesstime=timeGenerator(60);
        		  CreateAccDao.SaveTokens(accesstoken,refreshtoken,accesstime,Integer.parseInt(req.getParameter("uid")),(String)req.getParameter("clientid"));
        		  session.setAttribute("access_token", accesstoken);
        		  session.setAttribute("refresh_token", refreshtoken);
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
        		   resp.getWriter().printf("{access_token:%s,refresh_token:%s,expires_in_sec:%d}",session.getAttribute("access_token"),session.getAttribute("refresh_token"),3600);
        	   }
        	   else
        	   {
        		   resp.getWriter().print("Called for Code");
        	   }
           }
           
           // Get user profile details(READ SCOPE)
           public static void getUserProfileDetails(HttpServletRequest req,HttpServletResponse resp) throws NumberFormatException, SQLException, ParseException, ClassNotFoundException, IOException
           {
        	   if(CreateAccDao.ValidateAccessToken(req.getParameter("accesstoken"),Integer.parseInt(req.getParameter("uid")))==true)
        	   {
        		   CreateAccModel usersinfo=CreateAccDao.getUsers(Integer.parseInt(req.getParameter("uid")));
        		   resp.getWriter().printf("{name:%s,email:%s,phone:%s}",usersinfo.getName(),usersinfo.getEmail(),usersinfo.getPhone());
        	   }
        	   else
        		   resp.getWriter().print("InValid tokens");
           }
           
           //Revoke the AccessToken
           
           void RevokeToken(HttpServletRequest req,HttpServletResponse resp) throws ClassNotFoundException, SQLException, IOException
           {
        	   if(CreateAccDao.DeleteToken(req.getParameter("accesstoken"))==true)
        		   resp.getWriter().print("{success:true}");
        	   else
        		   resp.getWriter().print("{success:false}");
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
                  return generatedString;
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

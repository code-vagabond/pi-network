package org.rmi.server;



import org.rmi.common.ServerInterface;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import static java.lang.String.valueOf;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;


public class Server extends UnicastRemoteObject implements ServerInterface {
    
    public SessionSet s;
    public User user;
    public Friendship friendship;
    public Group group;
    public News news;
    public ServerFrame frame;
    public Post post;
    
    Server() throws RemoteException
    {
        super();
        frame       = new ServerFrame(this);
        frame.setResizable(false);
        frame.setVisible(true);
        
        news        = new News(this);
        s           = new SessionSet ();
        user        = new User (this);
        friendship  = new Friendship (this);
        group       = new Group (this);    
        post        = new Post(this);
    }
        
        
        public static void main(String[] args) {
        
        //Building RMI Connection according to example from lecture
        try {
            Registry rmiRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            //In case port registry fails, deregister the port and try to register again
            if (rmiRegistry != null){
                UnicastRemoteObject.unexportObject(rmiRegistry, true);
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            }
            //Else in case port registry doesn't fail, register port as normal
            else {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            }
        }
        catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        try {
            Naming.rebind("Server", new Server());
        }
        catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
       
        System.out.println("Press Enter to terminate the Server");
        Scanner in = new Scanner(System.in);
        
        
        
        String s = in.nextLine();
        System.exit(0);
    }

    public void stopRmiServer() {

        try{
            // Unregister ourself
            Naming.unbind("Server");

            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);

            System.out.println("CalculatorServer exiting.");
        }
        catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }catch(Exception e){}
    }
    
    
    
    
    public void Test (){ 
        int i = 0;
        //news.testRoutineGetNews();
        // news.testRoutineSendNews();
        //news.testRoutineSendNewsToGroups();
        //news.testRoutineMarkReadNews();
        //news.testRoutineDeleteNews();
        //news.testRoutinegetAllAdressees( );
        //news.testRoutineSendNewsToUsers();
    }    
    
    
    
    
    /**
     * Method logOut checks removes sessionID from the requestor 
     * @param sessionID sessionID saved on the frame
     */
   @Override
   public void logOut (String sessionID){
        String user  = this.user.getUsername(sessionID);
        frame.setlogText("Logout User: = " + user  );
        s.sessionIDUser.remove(sessionID);
    }
    
    @Override
    public int countOnlineUser (){
        int count = s.countOnlineUser();
        frame.setlogText( valueOf(count) + " user online" );
        return count;
    }
    
    @Override
    public DefaultListModel showOnlineUser () {
        return s.showOnlineUser();
    }
    /**
     * @param username inputted by user in Login Screen
     * @param password inputted by user in Login Screen
     * @return Method returns a sessionID in case it successfully verified 
     * the login in database or returns a specific string in case of failure
     */ 
   @Override
   public String verifyLogin (String username, String password) throws SQLException {
        if (user.loginVerified(username, password)){
            String sessionID = s.genSession(username);
            frame.setlogText("Login User: " + username + " ID= " +sessionID);
            return sessionID;
        }
        
        else {
            return "0";
        }

    }
    @Override
    public boolean userExist (String username) throws SQLException{
        return user.userExist(username);
    }
        
    @Override
    public ArrayList<String> getInfo (String sessionID) throws SQLException{
        return user.getInfo(sessionID);
    }
    
    public ArrayList<String> getPublicInfo (String username) throws SQLException{
        return user.getPublicInfo (username);
    }
            
    public void updateUserInfo (String sessionID, String mail, String first, String last, String bd, String d) throws SQLException {
        user.updateUserInfo (sessionID, mail, first, last, bd, d);
    }
    
    public void changePassword (String sessionID, String old, String newpass) throws SQLException {
        user.changePassword(sessionID, old, newpass);
        String user  = this.user.getUsername(sessionID);
        frame.setlogText("User: " + user + " Change Password" );

    }
    
    @Override
    public void createUser  (String username, String password, String mail, String firstName, String lastName, String birthday, String description){
        user.createUser(username, password, mail, firstName, lastName, birthday, description);
    }


    @Override
    public void deleteUser (String sessionID){
        String us  = this.user.getUsername(sessionID);
        if (s.isTimedOut(sessionID)){
            frame.setlogText("User: " + us + " Timeout" );
            JOptionPane.showMessageDialog(null, "Session timed out, please login again");
        }
        else {  
            user.deleteUser(sessionID);
        }
    }
    
    @Override
    public DefaultListModel listUser () throws SQLException{
        return user.listUser();
    }

    @Override
    public boolean passwordVerfied (String password, String sessionID) throws SQLException{
        return user.passwordVerified(password, sessionID);
    }
    
    @Override
    public DefaultListModel searchUsername (String keyword) throws SQLException {
        return user.searchUsername(keyword);
    }
    
    @Override
    public DefaultListModel searchEmail (String keyword) throws SQLException {
        return user.searchEmail(keyword);
    }
    
    @Override
    public boolean isTimedOut (String sessionID) {
    
        String us  = this.user.getUsername(sessionID);
        boolean timeout = s.isTimedOut(sessionID);
        if (timeout) {
            
            frame.setlogText("User: " + us + " Timeout" );
        }
        return timeout;
    }
    
    
    
    
    @Override
    public void addFriend (String sessionID, String friend) throws SQLException {
        friendship.addFriend(sessionID, friend);
    }
    @Override
    public void unfriend (String sessionID, String friend) throws SQLException {
        friendship.unfriend (sessionID, friend);
    }
    
    @Override
    public DefaultListModel listFriendsFrom (String sessionID) throws SQLException {
        return friendship.listFriendsFrom(sessionID);
    }
    
    @Override
    public DefaultListModel listFriendsTo (String sessionID) throws SQLException {
        return friendship.listFriendsTo(sessionID);
    }
    
    @Override
    public DefaultListModel listMutualFriendships (String sessionID) throws SQLException {
        return friendship.listMutualFriendships(sessionID);
    }
    
    @Override
    public boolean isFriend (String sessionID, String name) throws SQLException {
        return friendship.isFriend(sessionID, name);
    }
    
    
    
    
    
    @Override
    public boolean groupExists (String groupname) throws SQLException {
        return group.groupExists(groupname);
    }
    
    @Override
    public void addGroup (String sessionID, String gName, String gTopic) throws SQLException {
        group.addGroup(sessionID, gName, gTopic);
    }

    @Override
    public boolean isMember (String sessionID, String gName) throws SQLException {
        return group.isMember(sessionID, gName);
    }
    
    @Override
    public void joinGroup (String sessionID, String gName) throws SQLException {
        group.joinGroup(sessionID, gName);
    }
    
    @Override
    public DefaultListModel listMember (String gName) throws SQLException {
        return group.listMember(gName);
    }
    
    @Override
    public String getGroupTopic (String gName) throws SQLException {
        return group.getGroupTopic(gName);
    }
    
    @Override
    public int countMember (String gName) throws SQLException {
        return group.countMember(gName);
    }
    
    @Override
    public void leaveGroup (String sessionID, String gName) throws SQLException {
        group.leaveGroup(sessionID, gName);
    }
    
    @Override
    public DefaultListModel searchGroup (String keyword) throws SQLException {
        return group.searchGroup(keyword);
    }
    
    @Override
    public DefaultListModel listAllGroup () throws SQLException {
        return group.listAllGroup();
    }
    
    @Override
    public DefaultListModel listMyGroups (String sessionID) throws SQLException {
        return group.listMyGroups(sessionID);
    }
    
    // Call the News methode
    @Override
    public boolean sendNews (String sessionID, String toUser, String message, String newsTitle ){
        return news.sendNews (sessionID, toUser, message, newsTitle );
    }
    
    @Override
    public boolean sendNewsToUsers(String sessionID, DefaultListModel <String> userList, String message, String newsTitle ){
        return news.sendNewsToUsers(sessionID, userList, message, newsTitle );
    }
    
    @Override
    public boolean sendNewsToGroups (String sessionID, DefaultListModel <String> groupList, String message, String newsTitle ){
        return news.sendNewsToGroups (sessionID, groupList, message, newsTitle );    
    }
    
    @Override
    public DefaultListModel getAllNews(String sessionID, String sortOrder  ){
        return news.getAllNews (sessionID, sortOrder  );
    }
    
    @Override
    public boolean markReadNews (String sessionID, int news_number){
        return news.markReadNews (sessionID, news_number);
    }
    
    @Override
    public boolean deleteNews (String sessionID, int newsNumber )
    {
        return news.deleteNews(sessionID, newsNumber );
    }
    
    @Override
    public DefaultListModel<String> getAllAdressees(String sessionID)
    {
        return news.getAllAdressees(sessionID );
    }
    
    
    
    
    //Post
    public void submitPost (String sessionid, String tit, String con, int pub) throws SQLException {
        post.submitPost (sessionid, tit, con, pub);
    }
    
    public void deletePost (String sessionid, int id) throws SQLException{
        post.deletePost(sessionid, id);
    }
    
    public ArrayList <String> getPosts (String sessionid) throws SQLException {
        return  post.getPosts(sessionid);
    }
    
    public DefaultListModel <String[]> viewPosts (String sessionid) throws SQLException {
        return post.viewPosts(sessionid);
    }
    
    public ArrayList <String> getEditablePosts (String sessionid) throws SQLException {
        return post.getEditablePosts(sessionid);
    }
    
    public DefaultListModel <String[]> viewEditablePosts (String sessionid) throws SQLException {
        return post.viewEditablePosts(sessionid);
    }
    
    public ArrayList<String> getSelectedPost (String sessionid, int postID) throws SQLException {
        return post.getSelectedPost(sessionid, postID);
    }
    
    public void editPost (String sessionid, int postID, String title, String content) throws SQLException {
        post.editPost(sessionid, postID, title, content);
    }
        
    public void editPublicity (String sessionid, int postID, int pub) throws SQLException {
        post.editPublicity (sessionid, postID, pub);
    }
    
    public ArrayList <String> searchPost (String sessionid, String submitter) throws SQLException {
        return post.searchPost(sessionid, submitter);
    }
    
    public String getVoteCount (String postID) throws SQLException {
        return post.getVoteCount(postID);
    }
    

    public void votePost (String sessionid, int postID) throws SQLException {
        post.votePost(sessionid, postID);
    }
    
    public void unvotePost (String sessionid, int postID) throws SQLException {
        post.unvotePost(sessionid, postID);
    }
} 
    
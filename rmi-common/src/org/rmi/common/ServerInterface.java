package org.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public interface ServerInterface extends Remote {
    public int countOnlineUser () throws RemoteException;
    public DefaultListModel showOnlineUser () throws RemoteException;
    public boolean passwordVerfied (String password, String sessionID) throws SQLException, RemoteException;
    public String verifyLogin(String username, String password) throws SQLException, RemoteException; 
    public void createUser (String username, String password, String email, String firstName, String lastName, String birthday, String description) throws SQLException, RemoteException;
    public boolean userExist (String username) throws SQLException, RemoteException;
    public void deleteUser (String sessionID) throws RemoteException;
    public DefaultListModel listUser () throws SQLException, RemoteException;
    public void logOut (String sessionID) throws RemoteException;
    public void updateUserInfo (String sessionID, String mail, String first, String last, String bd, String d) throws SQLException, RemoteException;
    public void changePassword (String sessionID, String old, String newpass) throws SQLException, RemoteException;
    ArrayList<String> getInfo (String sessionID) throws SQLException, RemoteException;
    ArrayList<String> getPublicInfo (String username) throws SQLException, RemoteException;
    public DefaultListModel searchUsername (String keyword) throws SQLException, RemoteException;
    public DefaultListModel searchEmail (String keyword) throws SQLException, RemoteException;
    public boolean isTimedOut (String sessionID) throws RemoteException;    
    public void addFriend (String sessionID, String friend) throws SQLException, RemoteException;
    public void unfriend (String sessionID, String friend) throws SQLException, RemoteException;
    public DefaultListModel listFriendsFrom (String sessionID) throws SQLException, RemoteException;
    public DefaultListModel listFriendsTo (String sessionID) throws SQLException, RemoteException;
    public DefaultListModel listMutualFriendships (String sessionID) throws SQLException, RemoteException;
    public boolean isFriend (String sessionID, String name) throws SQLException, RemoteException;
    public boolean groupExists (String groupname) throws SQLException, RemoteException;
    public void addGroup (String sessionID, String gName, String gTopic) throws SQLException, RemoteException;
    public boolean isMember (String sessionID, String gName) throws SQLException, RemoteException;
    public void joinGroup (String sessionID, String gName) throws SQLException, RemoteException;
    public DefaultListModel listMember (String gName) throws SQLException, RemoteException;
    public String getGroupTopic (String gName) throws SQLException, RemoteException;
    public int countMember (String gName) throws SQLException, RemoteException;
    public void leaveGroup (String sessionID, String gName) throws SQLException, RemoteException;
    public DefaultListModel searchGroup (String keyword) throws SQLException, RemoteException;
    public DefaultListModel listAllGroup () throws SQLException, RemoteException;
    public DefaultListModel listMyGroups (String sessionID) throws SQLException, RemoteException;
    

    // News
    public boolean sendNews (String sessionID, String toUser,   String news, String newsTitle ) throws RemoteException;
    public boolean sendNewsToUsers  (String sessionID, DefaultListModel <String> userList, String news, String newsTitle ) throws RemoteException;
    public boolean sendNewsToGroups (String sessionID, DefaultListModel <String> groupList, String news, String newsTitle ) throws RemoteException;
    public DefaultListModel getAllNews (String sessionID, String sortOrder  ) throws RemoteException;
    public boolean markReadNews (String sessionID, int news_number) throws RemoteException;
    public boolean deleteNews (String sessionID, int newsNumber ) throws RemoteException;
    public DefaultListModel<String> getAllAdressees(String sessionID) throws RemoteException;
    
    
    //Post
    public void submitPost (String sessionid, String tit, String con, int pub) throws SQLException, RemoteException;
    public void deletePost (String sessionid, int id) throws SQLException, RemoteException;
    public DefaultListModel <String[]> viewPosts (String sessionid) throws SQLException, RemoteException;
    public DefaultListModel <String[]> viewEditablePosts (String sessionid) throws SQLException, RemoteException;
    public ArrayList<String> getSelectedPost (String sessionid, int postID) throws SQLException, RemoteException;
    public void editPost (String sessionid, int postID, String title, String content) throws SQLException, RemoteException;
    public void editPublicity (String sessionid, int postID, int pub) throws SQLException, RemoteException;
    public DefaultListModel <String[]> viewSearchPostResult (String sessionid, String bySubmitter) throws SQLException, RemoteException;
    public String getVoteCount (String postID) throws SQLException, RemoteException;
    public void votePost (String sessionid, int postID) throws SQLException, RemoteException;
    public void unvotePost (String sessionid, int postID) throws SQLException, RemoteException;

}


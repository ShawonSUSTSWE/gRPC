package service;

import com.demo.grpc.User;
import com.demo.grpc.userGrpc;
import hasher.PasswordHasher;
import io.grpc.stub.StreamObserver;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

public class UserService extends userGrpc.userImplBase {

    String databaseURL = "jdbc:mysql://localhost:3306/socialmediauser";
    String user = "root";
    String pass = "";


    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Override
    public void login(User.LoginRequest request, StreamObserver<User.LogResponse> responseObserver) throws SQLException, ClassNotFoundException {
        String userName = request.getUsername();
        String password = request.getPassword();

        ResultSet resultSet = checkLogInCredential(userName, password);

        User.LogResponse.Builder response = User.LogResponse.newBuilder();

        logger.info("Login attempt from " + userName );
        while (resultSet.next()) {
            if(resultSet.getInt(1) == 1) {
                response.setResCode(200).setMessage("SUCCESS");
            } else {
                response.setResCode(401).setMessage("Unauthorized");
            }
            break;
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void userRegistration(User.RegistrationRequest request, StreamObserver<User.RegistrationResponse> responseObserver) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        int userID = request.getUserid();
        String name = request.getName();
        String country = request.getCountry();
        String username = request.getUsername();
        String password = request.getPassword();

        ResultSet resultSet = checkPersonalInfo(userID);

        User.RegistrationResponse.Builder response = new User.RegistrationResponse.Builder();
        while (resultSet.next()) {
            if(resultSet.getInt(1) == 1) {
                response.setResponseMessage("User " + userID + " is already registered").setResponseCode(500);
            } else {
                Connection connection = getConnection(databaseURL, user, pass);
                PreparedStatement statement = connection.prepareStatement
                        ("INSERT INTO tbl_user_info VALUES('"+userID+"', '"+name+"', '"+country+"', '"+username+"')");
                statement.executeUpdate();
                PreparedStatement loginStatement = connection.prepareStatement("INSERT INTO tbl_user VALUES('"+username+"', '"+password+"')");
                loginStatement.executeUpdate();
                response.setResponseMessage(name +
                                " with User ID " + userID + " from " + country + " is now registered successfully").
                        setResponseCode(300);
            }
            break;
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkPersonalInfo(int userID) throws SQLException {
        Connection connection = getConnection(databaseURL, user, pass);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM tbl_user_info WHERE userID = ?)");
        statement.setInt(1, userID);
        return statement.executeQuery();
    }

    private ResultSet checkLogInCredential(String userName, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = getConnection(databaseURL, user, pass);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM tbl_user" +
                " WHERE userName =  BINARY '"+userName+"' && userpassword = BINARY '"+password+"')");

        return statement.executeQuery();
    }
}

package client;

import com.demo.grpc.User;
import com.demo.grpc.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 2046).usePlaintext().build();
        userGrpc.userBlockingStub userBlockingStub = new userGrpc.userBlockingStub(managedChannel);

        String userName, password;
        boolean authorized = false;
        Scanner input = new Scanner(System.in);

        while(!authorized) {
            System.out.println("Please enter your Username: ");
            userName = input.next();
            System.out.println("Please enter your Password: ");
            password = input.next();
            User.LoginRequest loginRequest = User.LoginRequest.newBuilder().setUsername(userName).setPassword(password).build();
            User.LogResponse loginResponse = userBlockingStub.login(loginRequest);
            //System.out.println(1);
            if (loginResponse.getResCode() == 200) {
                authorized = true;
            }
            System.out.println(loginResponse.getMessage());
        }

        System.out.println("Enter your UserID: ");
        int id = input.nextInt();
        String blank = input.nextLine();
        System.out.println("Enter your Name: ");
        String name = input.nextLine();
        System.out.println("Which country do you live in > ");
        String country = input.next();

        User.RegistrationRequest registrationRequest = User.RegistrationRequest.newBuilder().setUserid(id)
                .setName(name).setCountry(country).build();
        User.RegistrationResponse registrationResponse = userBlockingStub.userRegistration(registrationRequest);
        System.out.println(registrationResponse.getResponseMessage());
    }
}

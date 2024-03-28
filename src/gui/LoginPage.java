package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.ATMClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;





public class LoginPage extends Application {
    private static boolean hasAuthenticated = false;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("LOGIN");


            ATMClient atmClient = new ATMClient();
            Socket clientSocket = new Socket("localhost", 23456);
            ObjectOutputStream out =  new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            //Get authenticated
        if (!hasAuthenticated) {
            atmClient.authenticateBankToATM(in, out);
            atmClient.createBothKeys();
            hasAuthenticated =true;
        }
        // Creating a VBox layout for login components
        VBox loginBox = new VBox();
        loginBox.setSpacing(20); // Increased spacing between components
        loginBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("LOGIN");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Making the title bold
        titleLabel.setAlignment(Pos.TOP_CENTER); // Aligning the title towards the top
        titleLabel.setPadding(new Insets(20, 0, 0, 0)); // Adding padding to the top

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setMaxWidth(300); // Limiting width to fit inside the rectangle

        // Password field
        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300); // Limiting width to fit inside the rectangle

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight
        loginButton.setOnAction(e -> {
            // Print username and password to console
            String username = usernameField.getText();
            String password = passwordField.getText();

            //Executing the login process (ATM -> Bank)
            try
            {

                out.writeObject("L");
                atmClient.authenticateCustomer(in,out,username,password);

            } catch (UnknownHostException exp) {
                System.err.println("Don't know about host ");
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("Couldn't get I/O for the connection to " );
                System.exit(1);
            } catch (Exception ex1) {
                throw new RuntimeException(ex1);
            }//end of catch


            System.out.println("Username: " + username + ", Password: " + password);

            // Keep the existing routing behavior to navigate to the home screen
            HomePage homePage = HomePage.getInstance(atmClient,clientSocket,out,in);
            Stage homeStage = new Stage();
            homePage.start(homeStage);
            // Close the current stage (LoginPage)
            primaryStage.close();
        });

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight
        registerButton.setOnAction(e -> {
            RegisterPage registerPage = new RegisterPage(atmClient,clientSocket,out,in);
            Stage registerStage = new Stage();
            registerPage.start(registerStage);


            // Close the current stage (LoginPage)
            primaryStage.close();
        });

        // Adding nodes to the login VBox
        loginBox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, registerButton);

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius and border color
        Rectangle whiteRectangle = new Rectangle(400, 400);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius
        whiteRectangle.setStroke(Color.web("#dddddd")); // Adding border color

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, loginBox);

        // Centering the white rectangle
        StackPane.setAlignment(whiteRectangle, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600); // Creating a scene with fixed size
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage


    }



//    private void handleLogin(String username, String password) {
//        System.out.println("Username: " + username);
//        System.out.println("Password: " + password);
//        // Add logic to verify login credentials and navigate to the next scene
//    }

//    private void handleRegister(Stage primaryStage) {
//        RegisterPage registerPage = new RegisterPage();
//        registerPage.start(primaryStage);
//    }
//
//
//
//    private void showRegisterPage(Stage primaryStage) {
//        RegisterPage registerPage = new RegisterPage();
//        registerPage.start(primaryStage);
//    }

    public static void main(String[] args) {
        //System.setProperty("java.library.path", "C:\\javafx-sdk-22\\lib");
        launch(args);



            //Need to assess the flow of GUI




    }
}

package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.concurrent.atomic.AtomicBoolean;


public class LoginPage extends Application {

    private static ATMClient atmClient;
    private static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static boolean hasAuthenticated = false;

    public LoginPage(){
        if (atmClient==null){
            try {
                atmClient = new ATMClient();
                clientSocket = new Socket("localhost", 23456);
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("LOGIN");

        AtomicBoolean isLoggedIn = new AtomicBoolean(false);

        //Get authenticated
        if (!hasAuthenticated) {
            atmClient.authenticateBankToATM(in, out);
            atmClient.createBothKeys();
            hasAuthenticated = true;
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
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();
                out.writeObject("L");
                isLoggedIn.set(atmClient.authenticateCustomer(in, out, username, password)); //T or F
            } catch (IOException ex) {
                System.out.println("If it reaches here, assume you got I/O hostname exception");
                throw new RuntimeException(ex);
            }

            // Keep the existing routing behavior to navigate to the home screen
            HomePage homePage = HomePage.getInstance(atmClient, clientSocket, out, in);
            Stage homeStage = new Stage();
            if (isLoggedIn.get()) {
                homePage.start(homeStage);
                // Close the current stage (LoginPage)
                primaryStage.close();
            } else {
                // Displaying login failed message in red
                Label loginFailedLabel = new Label("Login Failed! Please enter correct credentials!");
                loginFailedLabel.setTextFill(Color.RED);
                loginBox.getChildren().add(loginFailedLabel);
            }
        });

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight
        registerButton.setOnAction(e -> {
            RegisterPage registerPage = new RegisterPage(atmClient, clientSocket, out, in);
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

    public static void main(String[] args) {
        launch(args);
    }
}

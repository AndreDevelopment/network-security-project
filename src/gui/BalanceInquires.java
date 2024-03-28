package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.ATMClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BalanceInquires extends Application {

    private ATMClient atmClient;
    private Socket clientSocket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public BalanceInquires(ATMClient atmClient, Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) {
        this.atmClient = atmClient;
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BALANCE INQUIRES");

        // Creating a VBox layout for balanceInquires page components
        VBox balanceInquiresBox = new VBox();
        balanceInquiresBox.setSpacing(20); // Increased spacing between components
        balanceInquiresBox.setAlignment(Pos.CENTER);

        // Creating a blue rectangle background covering the scene
        Rectangle blueBackground = new Rectangle(800, 600, Color.web("#c6e2ff"));

        // Load the home image
        Image homeImage = new Image(getClass().getResourceAsStream("/gui/images/home.png"));

        // Create an ImageView for the home image
        ImageView homeImageView = new ImageView(homeImage);
        homeImageView.setFitWidth(30); // Set the width of the image
        homeImageView.setFitHeight(30); // Set the height of the image

        // Load the home image
        Image logoutImage = new Image(getClass().getResourceAsStream("/gui/images/logout.png"));

        // Create an ImageView for the home image
        ImageView logoutImageView = new ImageView(logoutImage);
        logoutImageView.setFitWidth(30); // Set the width of the image
        logoutImageView.setFitHeight(30); // Set the height of the image

        // Creating a white rectangle background with border radius
        Rectangle whiteRectangle = new Rectangle(600, 400);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius

        // Adding border to the white rectangle
        whiteRectangle.setStroke(Color.web("#999999")); // Border color
        whiteRectangle.setStrokeWidth(2); // Border width

        // Creating a blue rectangle for "Current Balance"
        Rectangle currentBalanceRectangle = new Rectangle(598, 70, Color.web("#c6e2ff"));

        // Title: "CURRENT BALANCE"
        Label titleLabel = new Label("CURRENT BALANCE");
        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK); // Setting text color to black

        // Centering the title in the blue rectangle
        StackPane titlePane = new StackPane();
        titlePane.getChildren().addAll(currentBalanceRectangle, titleLabel);

        //I'm getting balance from ATM Client
        String currentBalance;
        try {
            out.writeObject("C");
            currentBalance = atmClient.checkBalance(in,out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Label: "Current Balance"
        Label currentBalanceLabel = new Label(currentBalance);
        currentBalanceLabel.setFont(new Font(16));

        // Button: "Home screen"
        Button homescreenButton = new Button("Home Screen");
        homescreenButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");

        // Event handler for the "Home Screen" button
        homescreenButton.setOnAction(e -> {

            System.out.println("Navigating to Home Screen...");
            // Create an instance of HomePage and show its stage
            HomePage homePage = HomePage.getInstance(atmClient,clientSocket,out,in);
            Stage homeStage = new Stage();
            homePage.start(homeStage);

            // Close the current stage (BalanceInquires)
            primaryStage.close();
        });

//        // Button: "Logout"
//        Button logoutButton = new Button("Logout");
//        logoutButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");
//
//        // Event handler for the "Logout" button
//        logoutButton.setOnAction(e -> {
//            // Create an instance of LoginPage and show its stage
//            LoginPage loginPage = new LoginPage();
//            Stage loginStage = new Stage();
//            loginPage.start(loginStage);
//
//            // Close the current stage (BalanceInquires)
//            primaryStage.close();
//        });

        // Adding nodes to the balanceInquires VBox
        balanceInquiresBox.getChildren().addAll(titlePane, currentBalanceLabel);

        // Setting the alignment of the buttons
        HBox.setMargin(homescreenButton, new Insets(0, 10, 0, 0)); // Adding margin to the right of the "Home Screen" button
        //HBox.setMargin(logoutButton, new Insets(0, 0, 0, 10)); // Adding margin to the left of the "Logout" button

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, balanceInquiresBox);

        // Centering the balanceInquires VBox
        StackPane.setAlignment(balanceInquiresBox, Pos.CENTER_LEFT);

        // Moving logoutImageView to the top right corner
        StackPane.setAlignment(logoutImageView, Pos.TOP_RIGHT);
        StackPane.setMargin(logoutImageView, new Insets(20, 20, 0, 0)); // Setting margin to move it slightly down and left

        // Create a button from the logoutImageView
        Button logoutButton = new Button();
        logoutButton.setGraphic(logoutImageView);
        logoutButton.setOnAction(e -> showLoginPage(primaryStage));

        // Adding logoutButton to the root StackPane
        root.getChildren().add(logoutButton);
        StackPane.setAlignment(logoutButton, Pos.TOP_RIGHT);
        StackPane.setMargin(logoutButton, new Insets(20)); // Setting margin to move it slightly down and left

        // Moving homeImageView to the top left corner
        StackPane.setAlignment(homeImageView, Pos.TOP_LEFT);
        StackPane.setMargin(homeImageView, new Insets(20, 0, 0, 20)); // Setting margin to move it slightly down

        // Create a button from the homeImageView
        Button homeButton = new Button();
        homeButton.setGraphic(homeImageView);
        homeButton.setOnAction(e -> showHomePage(primaryStage));

        // Adding homeButton to the root StackPane
        root.getChildren().add(homeButton);
        StackPane.setAlignment(homeButton, Pos.TOP_LEFT);
        StackPane.setMargin(homeButton, new Insets(20)); // Setting margin to move it slightly down and right


        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    // Method to show the home page
    private void showLoginPage(Stage primaryStage) {
        LoginPage loginPage = new LoginPage();
        try {
            loginPage.start(primaryStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showHomePage(Stage primaryStage) {
        HomePage homePage = HomePage.getInstance(atmClient,clientSocket,out,in);
        homePage.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

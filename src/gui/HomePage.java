package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.ATMClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HomePage extends Application {
    private Label actionLabel; // Declare actionLabel as an instance variable


    private static ATMClient atmClient;
    private static Socket clientSocket;

    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    private static HomePage instance;

    private HomePage() {

    }

    public static HomePage getInstance(ATMClient atm, Socket client, ObjectOutputStream outSt, ObjectInputStream inSt){
        if (instance==null) {
            instance = new HomePage();
            atmClient = atm;
            clientSocket = client;
            out = outSt;
            in = inSt;
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("HOME");

        // Creating a VBox layout for home page components
        VBox homeBox = new VBox();
        homeBox.setSpacing(20); // Increased spacing between components
        homeBox.setAlignment(Pos.CENTER); // Center align the components

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(800, 600, Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius
        Rectangle whiteRectangle = new Rectangle(655, 480);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius

        // Load the home image
        Image logoutImage = new Image(getClass().getResourceAsStream("/gui/images/logout.png"));

        // Create an ImageView for the home image
        ImageView logoutImageView = new ImageView(logoutImage);
        logoutImageView.setFitWidth(30); // Set the width of the image
        logoutImageView.setFitHeight(30); // Set the height of the image

        // Load the avatar image
        Image avatarImage = new Image(getClass().getResourceAsStream("/gui/images/avatar.png"));

        // Create an ImageView for the avatar image
        ImageView avatarImageView = new ImageView(avatarImage);
        avatarImageView.setFitWidth(100); // Set the width of the image
        avatarImageView.setFitHeight(100); // Set the height of the image

        // Optional: Set the circle clip to make the image appear as a circle
        avatarImageView.setClip(new Circle(50, 50, 50));

        // Optional: Add a border to the image
        avatarImageView.setStyle("-fx-border-color: black; -fx-border-width: 2;");


        // Text: "Select an action to perform"
        actionLabel = new Label("Select an action to perform");
        actionLabel.setFont(new Font(16));

        // Buttons
        HBox buttonBox = new HBox(40); // Increased spacing between buttons
        buttonBox.setAlignment(Pos.CENTER);
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdrawal");
        Button balanceButton = new Button("Balance Inquiry");

        // Setting button styles
        String buttonStyle = "-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 60px; -fx-font-weight: bold;";
        depositButton.setStyle(buttonStyle);
        withdrawButton.setStyle(buttonStyle);
        balanceButton.setStyle(buttonStyle);

        // Event handlers for the buttons
        depositButton.setOnAction(e -> showDepositPage(primaryStage));
        withdrawButton.setOnAction(e -> showWithdrawalPage(primaryStage));
        balanceButton.setOnAction(e -> {

            showBalanceInquiryPage(primaryStage);



        });

        // Adding nodes to the home VBox
        homeBox.getChildren().addAll(avatarImageView, actionLabel, buttonBox);
        buttonBox.getChildren().addAll(depositButton, withdrawButton, balanceButton);

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, homeBox);

        // Centering the home VBox
        StackPane.setAlignment(homeBox, Pos.CENTER);

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

        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    private void showLoginPage(Stage primaryStage)  {
        LoginPage loginPage = new LoginPage();
        try {
            loginPage.start(primaryStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to update the content for the deposit page
    private void showDepositPage(Stage primaryStage) {
        DepositPage depositPage = new DepositPage(atmClient,clientSocket,out,in);
        depositPage.start(primaryStage);
    }

    // Method to update the content for the withdrawal page
    private void showWithdrawalPage(Stage primaryStage) {
        WithdrawalPage withdrawalPage = new WithdrawalPage(atmClient,clientSocket,out,in);
        withdrawalPage.start(primaryStage);
    }

    // Method to update the content for the balance inquiry page
    private void showBalanceInquiryPage(Stage primaryStage) {
        BalanceInquires balanceInquires = new BalanceInquires(atmClient,clientSocket,out,in);
        balanceInquires.start(primaryStage);
    }



    // Other methods...

    public static void main(String[] args) {
        launch(args);
    }
}

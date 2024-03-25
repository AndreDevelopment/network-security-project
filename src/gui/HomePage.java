package gui;

import javafx.application.Application;
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

public class HomePage extends Application {
    private Label actionLabel; // Declare actionLabel as an instance variable

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Home");

        // Creating a VBox layout for home page components
        VBox homeBox = new VBox();
        homeBox.setSpacing(20); // Increased spacing between components
        homeBox.setAlignment(Pos.CENTER);

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(800, 600, Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius
        Rectangle whiteRectangle = new Rectangle(655, 480);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius

        // Creating a circle for human avatar
        Circle avatarCircle = new Circle(50, Color.LIGHTBLUE); // Sample color
        avatarCircle.setStroke(Color.BLACK);

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
        balanceButton.setOnAction(e -> showBalanceInquiryPage(primaryStage));

        // Adding nodes to the home VBox
        homeBox.getChildren().addAll(avatarCircle, actionLabel, buttonBox);
        buttonBox.getChildren().addAll(depositButton, withdrawButton, balanceButton);

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, homeBox);

        // Centering the home VBox
        StackPane.setAlignment(homeBox, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    // Method to update the content for the deposit page
    private void showDepositPage(Stage primaryStage) {
        DepositPage depositPage = new DepositPage();
        depositPage.start(primaryStage);
    }

    // Method to update the content for the withdrawal page
    private void showWithdrawalPage(Stage primaryStage) {
        WithdrawalPage withdrawalPage = new WithdrawalPage();
        withdrawalPage.start(primaryStage);
    }

    // Method to update the content for the balance inquiry page
    private void showBalanceInquiryPage(Stage primaryStage) {
        BalanceInquires balanceInquires = new BalanceInquires();
        balanceInquires.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class WithdrawalPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("WITHDRAWAL");

        // Creating a VBox layout for withdrawal page components
        VBox withdrawalBox = new VBox();
        withdrawalBox.setSpacing(20); // Increased spacing between components
        withdrawalBox.setAlignment(Pos.CENTER);

        // Creating a blue rectangle background covering the scene
        Rectangle blueBackground = new Rectangle(800, 600, Color.web("#c6e2ff"));

        // Load the home image
        Image homeImage = new Image(getClass().getResourceAsStream("/gui/images/home.png"));

        // Create an ImageView for the home image
        ImageView homeImageView = new ImageView(homeImage);
        homeImageView.setFitWidth(30); // Set the width of the image
        homeImageView.setFitHeight(30); // Set the height of the image

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

        // Title: "WITHDRAWAL"
        Label titleLabel = new Label("WITHDRAWAL");
        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK); // Setting text color to black

        // Centering the title in the blue rectangle
        StackPane titlePane = new StackPane();
        titlePane.getChildren().addAll(currentBalanceRectangle, titleLabel);

        // Label: "Current Balance"
        Label currentBalanceLabel = new Label("Current Balance");
        currentBalanceLabel.setFont(new Font(16));

        // Label: "Withdrawal amount"
        Label withdrawalAmountLabel = new Label("Enter amount to withdraw");
        withdrawalAmountLabel.setFont(new Font(16));

        // Text field for withdrawal amount
        TextField withdrawalMoneyField = new TextField();
        withdrawalMoneyField.setPromptText("Enter Withdrawal Amount");
        withdrawalMoneyField.setPromptText("Enter Withdrawal Amount");
        withdrawalMoneyField.setMaxWidth(200);

        // Button: "Withdrawal"
        Button withdrawalButton = new Button("Withdraw");
        withdrawalButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");

        // Adding nodes to the withdrawal VBox
        withdrawalBox.getChildren().addAll(titlePane, currentBalanceLabel, withdrawalAmountLabel, withdrawalMoneyField, withdrawalButton);

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, withdrawalBox);

        // Centering the withdrawal VBox
        StackPane.setAlignment(withdrawalBox, Pos.CENTER_LEFT);

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
    private void showHomePage(Stage primaryStage) {
        HomePage homePage = new HomePage();
        homePage.start(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

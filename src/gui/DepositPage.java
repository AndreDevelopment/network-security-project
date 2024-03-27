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

public class DepositPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DEPOSIT");

        // Creating a VBox layout for deposit page components
        VBox depositBox = new VBox();
        depositBox.setSpacing(20); // Increased spacing between components
        depositBox.setAlignment(Pos.CENTER);

        // Creating a blue rectangle background covering the scene
        Rectangle blueBackground = new Rectangle(800, 600, Color.web("#c6e2ff"));

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

        // Title: "DEPOSIT"
        Label titleLabel = new Label("DEPOSIT");
        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK); // Setting text color to black

        // Centering the title in the blue rectangle
        StackPane titlePane = new StackPane();
        titlePane.getChildren().addAll(currentBalanceRectangle, titleLabel);

        // Label: "Current Balance"
        Label currentBalanceLabel = new Label("Current Balance");
        currentBalanceLabel.setFont(new Font(16));

        // Label: "Deposit amount"
        Label depositAmountLabel = new Label("Enter amount to deposit");
        depositAmountLabel.setFont(new Font(16));

        // Text field for deposit amount
        TextField depositMoneyField = new TextField();
        depositMoneyField.setPromptText("Enter Deposit Amount");
        depositMoneyField.setMaxWidth(200);

        // Button: "Deposit"
        Button depositButton = new Button("Deposit");
        depositButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");

        // Adding nodes to the deposit VBox
        depositBox.getChildren().addAll(titlePane, currentBalanceLabel, depositAmountLabel, depositMoneyField, depositButton);

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, depositBox);

        // Centering the deposit VBox
        StackPane.setAlignment(depositBox, Pos.CENTER_LEFT);

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
        StackPane.setMargin(homeImageView, new Insets(20, 0, 0, 20)); // Setting margin to move it slightly down and right

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

    // Method to show the login page
    private void showLoginPage(Stage primaryStage) {
        LoginPage loginPage = new LoginPage();
        loginPage.start(primaryStage);
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

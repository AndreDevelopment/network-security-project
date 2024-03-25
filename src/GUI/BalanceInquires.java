package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BalanceInquires extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BALANCE INQUIRES");

        // Creating a VBox layout for balanceInquires page components
        VBox balanceInquiresBox = new VBox();
        balanceInquiresBox.setSpacing(20); // Increased spacing between components
        balanceInquiresBox.setAlignment(Pos.CENTER);

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

        // Title: "CURRENT BALANCE"
        Label titleLabel = new Label("CURRENT BALANCE");
        titleLabel.setFont(new Font(24));
        titleLabel.setTextFill(Color.BLACK); // Setting text color to black

        // Centering the title in the blue rectangle
        StackPane titlePane = new StackPane();
        titlePane.getChildren().addAll(currentBalanceRectangle, titleLabel);

        // Label: "Current Balance"
        Label currentBalanceLabel = new Label("Current Balance");
        currentBalanceLabel.setFont(new Font(16));

        // Button: "Home screen"
        Button homescreenButton = new Button("Home Screen");
        homescreenButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");

        // Event handler for the "Home Screen" button
        homescreenButton.setOnAction(e -> {
            // Create an instance of HomePage and show its stage
            HomePage homePage = new HomePage();
            Stage homeStage = new Stage();
            homePage.start(homeStage);

            // Close the current stage (BalanceInquires)
            primaryStage.close();
        });

        // Button: "Logout"
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 150px; -fx-min-height: 40px; -fx-font-weight: bold;");

        // Event handler for the "Logout" button
        logoutButton.setOnAction(e -> {
            // Create an instance of LoginPage and show its stage
            LoginPage loginPage = new LoginPage();
            Stage loginStage = new Stage();
            loginPage.start(loginStage);

            // Close the current stage (BalanceInquires)
            primaryStage.close();
        });

        // Adding nodes to the balanceInquires VBox
        balanceInquiresBox.getChildren().addAll(titlePane, currentBalanceLabel, homescreenButton, logoutButton);

        // Setting the alignment of the buttons
        HBox.setMargin(homescreenButton, new Insets(0, 10, 0, 0)); // Adding margin to the right of the "Home Screen" button
        HBox.setMargin(logoutButton, new Insets(0, 0, 0, 10)); // Adding margin to the left of the "Logout" button

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, balanceInquiresBox);

        // Centering the balanceInquires VBox
        StackPane.setAlignment(balanceInquiresBox, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}

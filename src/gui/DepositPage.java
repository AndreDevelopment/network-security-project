package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
        StackPane.setAlignment(depositBox, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}

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
import javafx.stage.Stage;

public class RegisterPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEW USER REGISTRATION");

        // Creating a GridPane layout for registration components
        GridPane registrationGrid = new GridPane();
        registrationGrid.setVgap(20); // Increased vertical gap between components
        registrationGrid.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("NEW USER REGISTRATION");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Making the title bold
        titleLabel.setAlignment(Pos.TOP_CENTER); // Aligning the title towards the top
        titleLabel.setPadding(new Insets(20, 0, 0, 0)); // Adding padding to the top
        registrationGrid.add(titleLabel, 0, 0, 2, 1); // Adding the title to the grid

        // Name field
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        registrationGrid.add(nameLabel, 0, 1); // Adding the label to the grid
        registrationGrid.add(nameField, 1, 1); // Adding the text field to the grid

        // Username field
        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        registrationGrid.add(usernameLabel, 0, 2); // Adding the label to the grid
        registrationGrid.add(usernameField, 1, 2); // Adding the text field to the grid

        // Password field
        Label passwordLabel = new Label("Password");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        registrationGrid.add(passwordLabel, 0, 3); // Adding the label to the grid
        registrationGrid.add(passwordField, 1, 3); // Adding the text field to the grid

        // Re-Password field
        Label reEnterPassBoxLabel = new Label("Re-Enter Password  ");
        TextField reEnterPassBoxField = new TextField();
        reEnterPassBoxField.setPromptText("Re-Enter Password");
        registrationGrid.add(reEnterPassBoxLabel, 0, 4); // Adding the label to the grid
        registrationGrid.add(reEnterPassBoxField, 1, 4); // Adding the text field to the grid

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight
        registerButton.setOnAction(e -> {
            // Create an instance of LoginPage and show its stage
            LoginPage loginPage = new LoginPage();
            Stage loginStage = new Stage();
            loginPage.start(loginStage);
            // Close the current stage (RegisterPage)
            primaryStage.close();
        });

        // Creating an HBox to center the register button
        HBox buttonBox = new HBox(registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        registrationGrid.add(buttonBox, 0, 5, 2, 1); // Adding the register button to the grid

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(1000, 800, Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius
        Rectangle whiteRectangle = new Rectangle(400, 400);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, registrationGrid);

        // Centering the white rectangle
        StackPane.setAlignment(whiteRectangle, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }



    public static void main(String[] args) {
        System.setProperty("java.library.path", "C:\\javafx-sdk-22\\lib");
        launch(args);
    }
}

package demo;

// JavaFX imports
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;  
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
// Other imports
import java.net.URL;


public class Main extends Application {
    private Stage primaryStage; // The primary window of the application
    private VBox root; // The root container for UI elements within the scene
    private double initFontSize = 14.0;

    private Button startSpellCheckButton, browseButton;   // For input area items
    private VBox fileContentsContainer;
    private TextField filePathField;
    
    private HBox spellCheckerContainer; // For spell checker items (suggestions, correction options)
    private String selectedSuggWord = "";
    private Button replaceButton, replaceAllButton, ignoreButton, ignoreAllButton, addToDictButton, deleteTextButton;
    ListView<String> suggestListView;

    private TextArea fileTextField;     // For displaying file text and manually editing
    private String fileContents;    // Contains contents of document and displays in fileTextField text area
    private Button editTextFieldButton, saveTextFieldButton, undoTextEditButton;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Assign the primary stage to the class variable
        primaryStage.setResizable(false); // Disable window resizing
        root = new VBox(); // The main container that organizes UI elements vertically (Vbox) 

        // **Create display areas here**
        createMenuBar();
        createInputContainer(); // Sets up the file input section
        createSpellCheckerContainer();; // Sets up the spell checker: suggestions and spellchecking options 
        createfileContentsContainer(); // Sets up the area to display file contents
        createFooterContainer();    // Sets up footer area including progress, radio-buttons, in future spelling stats
        
        // Create a scene
        Scene scene = new Scene(root, 720, 720); // The view containing UI elements
        applyStylesheet("styles.css", scene); 
        
        // Set the scene for the primary stage
        primaryStage.setScene(scene);
        
        // Set the title of the window
        primaryStage.setTitle("Spell-Checker App");
        
        // Set the application icon
        Image icon = new Image(getClass().getResourceAsStream("/program_icon.png"));
        primaryStage.getIcons().add(icon);

        // Show the window
        primaryStage.show();
    }

    private void applyStylesheet(String stylesheet, Scene scene) {
        if (scene != null) {
            URL resource = getClass().getResource(stylesheet);
            if (resource != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(resource.toExternalForm());
            } else {
                System.out.println("Resource not found: " + stylesheet);  
            }
        }
    }

    
    // Menubar
    private void createMenuBar() {
        // Create a menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menubar");
    
        // Create a "File" menu
        Menu fileMenu = new Menu("File");
        fileMenu.getStyleClass().add("file-menu");
    
        // Create "Open" and "Exit" menu items
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.getStyleClass().add("file-menu-open");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.getStyleClass().add("file-menu-exit");
    
        // Set actions for the "Open" and "Exit" menu items
        openMenuItem.setOnAction(e -> openFile(primaryStage));
        exitMenuItem.setOnAction(e -> exitApplication());
    
        // Add menu items to the "File" menu
        fileMenu.getItems().addAll(openMenuItem, exitMenuItem);
    
        // Create a "View" menu
        Menu viewMenu = new Menu("View");
        viewMenu.getStyleClass().add("view-menu");
    
        // Create "Increase Text Size," "Decrease Text Size," and "Reset Text Size" menu items
        MenuItem increaseTextSizeMenuItem = new MenuItem("Increase Text Size");
        MenuItem decreaseTextSizeMenuItem = new MenuItem("Decrease Text Size");
        MenuItem resetTextSizeMenuItem = new MenuItem("Reset Text Size");
    
        // Set actions for the "Increase Text Size," "Decrease Text Size," and "Reset Text Size" menu items
        increaseTextSizeMenuItem.setOnAction(e -> adjustTextSize(2.0));
        decreaseTextSizeMenuItem.setOnAction(e -> adjustTextSize(-2.0));
        resetTextSizeMenuItem.setOnAction(e -> {
            initFontSize = 14.0;
            String fontSizeStyle = "-fx-font-size: " + initFontSize + "px;";
            root.setStyle(fontSizeStyle);
        });
        
    
        // Add menu items to the "View" menu
        viewMenu.getItems().addAll(increaseTextSizeMenuItem, decreaseTextSizeMenuItem, resetTextSizeMenuItem);
    
        // Add the "File" and "View" menus to the menu bar
        menuBar.getMenus().addAll(fileMenu, viewMenu);
    
        root.getChildren().add(menuBar);
    }
    
    private void openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();

            if (filePath.toLowerCase().endsWith(".txt")) {
                // Handle the file open operation, e.g., read the file content and update your UI
                // Replace this with your actual file open logic
                filePathField.setText(filePath);
            } else {
                // Show an error message for an invalid file type
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Invalid File Type");
                alert.setHeaderText("Please enter a valid plain-text file.");
                alert.showAndWait();
                filePathField.clear();
            }
        }
    }

    private void exitApplication() {
        // Will include save file
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                primaryStage.close();
            }
        });
    }

    private void adjustTextSize(double pixelChange) {
        initFontSize += pixelChange;
        String fontSizeStyle = "-fx-font-size: " + initFontSize + "px;";
        root.setStyle(fontSizeStyle);
    }


    // Input container items include field text box and buttons
    private void createInputContainer() {
        // Create a VBox to hold the input group and text field
        Label titleLabel = new Label("File path:"); 
        VBox inputContainer = new VBox();
        inputContainer.getStyleClass().add("input-container");  // Example of adding a container, can add an ID too using .setId("inputContainer");

        // Create an HBox to group the "Browse," "Start Spell Check," and style-switching buttons
        HBox buttonGroup = new HBox();

        // Create a text field to display the selected file path
        filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setDisable(true);
        filePathField.setPrefWidth(720); // Set the width to match the window's width (can be added in css too)

        // Create a button to browse for a file
        browseButton = new Button("Browse");
        browseButton.setOnAction(e -> handleBrowseFile(primaryStage));

        // Create a button to start spell-check
        startSpellCheckButton = new Button("Start Spell Check");
        startSpellCheckButton.setDisable(true);
        startSpellCheckButton.setOnAction(e -> startSpellCheck());
        
        // Add the "Browse" and "Start Spell Check" buttons to the button group
        buttonGroup.getChildren().addAll(browseButton, startSpellCheckButton);


        // Add the button group and text field to the main VBox
        inputContainer.getChildren().addAll(titleLabel, filePathField, buttonGroup);
        // Add the main VBox to the root
        root.getChildren().addAll(inputContainer);
    }

    // Suggestion correction button options (replace, ignore) in here
    private void createSpellCheckerContainer(){
        spellCheckerContainer = new HBox();
        spellCheckerContainer.getStyleClass().add("spellchecker-container"); 
        spellCheckerContainer.setVisible(false);
    
        VBox suggestedWordsContainer = new VBox();
       

        Label suggestedWordsLabel = new Label("Suggested words:");
        suggestedWordsLabel.setId("label-suggested-words");
        suggestListView = new ListView<>();
        suggestListView.setId("suggested-word-list");
    
        // Example of suggested wordlist
        // Create an ObservableList to hold the suggested words
        ObservableList<String> suggestedWords = FXCollections.observableArrayList();
        suggestListView.setItems(suggestedWords);
        // Example of suggested words using a list
        suggestedWords.addAll("Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit");
        // Add an event handler to capture the selected word
        suggestListView.setOnMouseClicked(event -> {
            String selectedWord = suggestListView.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                selectedSuggWord = selectedWord;
                // You can perform any other actions with the selected word here
                System.out.println(selectedSuggWord);
            }
        });
        suggestedWordsContainer.getChildren().addAll(suggestedWordsLabel, suggestListView);

        VBox correctionContainer = new VBox(); // label and buttons
        correctionContainer.getStyleClass().add("correction-container");
        Label correctionLabel = new Label("Correction options:");
        correctionLabel.setId("label-correction-options");
        
        HBox correctionButtonsGroup = new HBox();   //(replace, ignore) buttons
        correctionButtonsGroup.getStyleClass().add("correction-button-group");
        
        // "Replace" and "Ignore" button groups
        VBox replaceButtonsGroup = new VBox();
        replaceButtonsGroup.getStyleClass().add("replace-button-group");
        replaceButton = new Button("Replace");
        replaceButton.setOnAction(e -> handleReplace());
        replaceAllButton = new Button("Replace All");
        replaceAllButton.setOnAction(e -> handleReplaceAll());
        replaceButtonsGroup.getChildren().addAll(replaceButton, replaceAllButton);

        VBox ignoreButtonGroup = new VBox();
        ignoreButtonGroup.getStyleClass().add("ignore-button-group");
        ignoreButton = new Button("Ignore");
        ignoreButton.setOnAction(e -> handleIgnore());
        ignoreAllButton = new Button("Ignore All");
        ignoreAllButton.setOnAction(e -> handleIgnoreAll());
        ignoreButtonGroup.getChildren().addAll(ignoreButton, ignoreAllButton);

        VBox addDeleteButtonGroup = new VBox();
        addDeleteButtonGroup.getStyleClass().add("add-delete-button-group");
        deleteTextButton = new Button("Delete");
        deleteTextButton.setOnAction(e -> handleDeleteError());
        addToDictButton = new Button("Add to Dictionary");
        addToDictButton.setOnAction(e -> handleAddToDict());
        addDeleteButtonGroup.getChildren().addAll(deleteTextButton, addToDictButton);


        correctionButtonsGroup.getChildren().addAll(replaceButtonsGroup, ignoreButtonGroup, addDeleteButtonGroup);
        correctionContainer.getChildren().addAll(correctionLabel, correctionButtonsGroup);

        spellCheckerContainer.getChildren().addAll(suggestedWordsContainer, correctionContainer);
        root.getChildren().add(spellCheckerContainer);
    }

    // Manual correction button options in here
    private void createfileContentsContainer() {
        Label titleLabel = new Label("File contents:");
        titleLabel.setId("label-file-contents");
        
        fileContentsContainer = new VBox(); // Outermost area, container for text area and buttons
        fileContentsContainer.getStyleClass().add("file-contents-container");
        fileContentsContainer.setVisible(false);
    
        fileTextField = new TextArea();
        fileTextField.setMinWidth(300);
        fileTextField.setWrapText(true);
        fileTextField.setEditable(false);
    
        HBox buttonGroup = new HBox(); // Grouping manual edit buttons
        buttonGroup.getStyleClass().add("edit-button-container");
        Label buttonGroupLabel = new Label ("Manual correction options:");
        buttonGroupLabel.setId("label-manual-correction-buttons");
    
    
        undoTextEditButton = new Button("Undo and Cancel");
        undoTextEditButton.setDisable(true);
        undoTextEditButton.setOnAction(e -> handleUndoAndCancel());
    
        editTextFieldButton = new Button("Edit");
        editTextFieldButton.setOnAction(e -> handleEnableEditing());
    
        saveTextFieldButton = new Button("Save");
        saveTextFieldButton.setDisable(true);
        saveTextFieldButton.setOnAction(e -> handleSaveEditChanges());
    
        buttonGroup.getChildren().addAll(buttonGroupLabel, editTextFieldButton, saveTextFieldButton, undoTextEditButton);
    
        // Add the label and children to the fileContentsContainer
        fileContentsContainer.getChildren().addAll(titleLabel, fileTextField, buttonGroup);
    
        root.getChildren().add(fileContentsContainer);
    }
    
    // Footer items include progress and radio-buttons. Will add error stats here. 
    private void createFooterContainer() {
        HBox footerContainer = new HBox();
        footerContainer.getStyleClass().add("footer-container");
        Label progressLabel = new Label("Progress:");
        progressLabel.setId("label-progress");

        // Create a ToggleGroup for the radio buttons
        ToggleGroup styleToggleGroup = new ToggleGroup();

        RadioButton lightModeButton = new RadioButton("Light Mode");
        lightModeButton.setToggleGroup(styleToggleGroup);
        
        RadioButton darkModeButton = new RadioButton("Dark Mode");
        darkModeButton.setToggleGroup(styleToggleGroup);
        darkModeButton.setSelected(true); // Dark mode is default for now
        root.getStyleClass().add("dark-mode");

        // Listener to toggle between styles
        styleToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == darkModeButton) {
                // Apply the dark mode stylesheet
                root.getStyleClass().remove("light-mode"); // Remove the light-mode class
                root.getStyleClass().add("dark-mode");
            } else if (newValue == lightModeButton) {
                // Apply the light mode stylesheet
                root.getStyleClass().remove("dark-mode"); // Remove the dark-mode class
                root.getStyleClass().add("light-mode");
            }
        });
        

        HBox toggleGroupBox = new HBox();
        toggleGroupBox.getStyleClass().add("toggle-group-box");

        toggleGroupBox.getChildren().addAll(lightModeButton, darkModeButton);
        footerContainer.getChildren().addAll(progressLabel, toggleGroupBox);

        root.getChildren().add(footerContainer);
}


    // Event handler functions

    // Opens a FileChooser and checks selected file is of valid type
    private void handleBrowseFile(Stage primaryStage) {
        // FileChooser fileChooser = new FileChooser();
        // fileChooser.setTitle("Open File");
        // java.io.File selectedFile = fileChooser.showOpenDialog(primaryStage);

        // if (selectedFile != null) {
        //     String filePath = selectedFile.getAbsolutePath();

        //     if (filePath.toLowerCase().endsWith(".txt")) {
        //         filePathField.setText(filePath);
        //         // Allow spell check to start after txt file is browsed
        //         startSpellCheckButton.setDisable(false);    
        //     } else {
        //         Alert alert = new Alert(AlertType.WARNING);
        //         alert.setTitle("Invalid File Type");
        //         alert.setHeaderText("Please enter a valid plain-text file.");
        //         alert.showAndWait();
        //         filePathField.clear();
        //     }
        // }

        // Instead of doing that all the time, just replace the path of a file of your choosing:
        startSpellCheckButton.setDisable(false);
        filePathField.setText("C:\\Users\\ryati\\Desktop\\testing.txt");
    }

    // Starts program when start is pressed after a valid file is selected
    private void startSpellCheck() {
        // Disable fieldpath form, browse button, and start button
        // Show file contents and spelling options
        filePathField.setDisable(true);
        browseButton.setDisable(true);
        startSpellCheckButton.setDisable(true);
        fileContentsContainer.setVisible(true);
        spellCheckerContainer.setVisible(true);
        // Read file contents from file path and show in text field
        fileContents = Document.readTextFile(filePathField.getText());
        fileTextField.setText(fileContents);
    }

    private void handleReplace() {
        // Implement the action for the "Replace" button here
    }

    private void handleReplaceAll() {
        // Implement the action for the "Replace All" button here
    }

    private void handleIgnore() {
        // Implement the action for the "Ignore" button here
    }

    private void handleIgnoreAll() {
        // Implement the action for the "Ignore All" button here
    }

    private void handleDeleteError() {
        // Implement the action for the "Delete" button here
    }

    private void handleAddToDict(){

    }

    private void handleEnableEditing() {
        fileTextField.setEditable(true);
        editTextFieldButton.setDisable(true); 
        deleteTextButton.setDisable(true);
        saveTextFieldButton.setDisable(false);
        undoTextEditButton.setDisable(false);

        // Disable the spell-check options
        replaceButton.setDisable(true);
        replaceAllButton.setDisable(true);
        ignoreButton.setDisable(true);
        ignoreAllButton.setDisable(true);
    }

    private void handleSaveEditChanges(){
        fileContents =fileTextField.getText();  // Rewriting the previous content before editing, original text is lost but not overwritten yet

        fileTextField.setEditable(false);
        editTextFieldButton.setDisable(false); 
        deleteTextButton.setDisable(false);
        saveTextFieldButton.setDisable(true);
        undoTextEditButton.setDisable(true);

        // Enable the spell-check options
        replaceButton.setDisable(false);
        replaceAllButton.setDisable(false);
        ignoreButton.setDisable(false);
        ignoreAllButton.setDisable(false);

        // Not saving to a file yet.
    }

    private void handleUndoAndCancel(){
        fileTextField.setText(fileContents); // Reverting back to previous content before edit
        undoTextEditButton.setDisable(true);
        saveTextFieldButton.setDisable(true);
        // Enable the spell-check options
        replaceButton.setDisable(false);
        replaceAllButton.setDisable(false);
        ignoreButton.setDisable(false);
        ignoreAllButton.setDisable(false);
        // Enable delete and edit buttons
        deleteTextButton.setDisable(false);
        editTextFieldButton.setDisable(false);
        fileTextField.setEditable(false); // Stop the file contents field from being editable
    }
}

package com.example.texteditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;

import java.io.File;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainController controller = fxmlLoader.getController();
        stage.setTitle("Text Editor: Primary window");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {controller.onExit(); Platform.exit();});
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Opens a file chooser dialog and returns the selected file.
     * @return The selected file
     */
    public static File getFile() {
        FileChooser fileChooser = new FileChooser();
        // specify allowed file types
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt", "*.py", "*.cpp", "*.java", "*.odt"));
        //set initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return fileChooser.showOpenDialog(null);
    }

    /**
     * Reads the contents of an .odt file and returns them as a String.
     * @return The contents of the .odt file
     */
    public static String readODT(File file) {
        try (OdfTextDocument doc = OdfTextDocument.loadDocument(file)) {
            OfficeTextElement officeText = doc.getContentRoot();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < officeText.getChildNodes().getLength(); i++) {
                if (officeText.getChildNodes().item(i) instanceof TextPElement) {
                    sb.append(((TextPElement) officeText.getChildNodes().item(i)).getTextContent()).append(System.lineSeparator());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Reads a file and returns its content.
     * @param file The file to read. Usually obtained from getFile().
     * @return The content of the file.
     */
    public static String readFile(File file) {
        if (FilenameUtils.getExtension(file.getName()).equals("odt")) {
            return readODT(file);
        }
        StringBuilder content = new StringBuilder();
        try {
            java.io.FileReader fileReader = new java.io.FileReader(file);
            java.io.BufferedReader bufferedReader = new java.io.BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            bufferedReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return content.toString();
    }

    /**
     * Opens a file chooser dialog and returns the File to save to.
     * @return The File to save to.
     */
    public static File chooseSaveFile() {
        FileChooser fileChooser = new FileChooser();
        // specify allowed file types
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        //set initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return fileChooser.showSaveDialog(null);
    }

    /**
     * Saves a file.
     * @param file The file to save. Usually obtained from chooseSaveFile() or getFile().
     * @param content The content to save.
     */
    public static void saveFile(File file, String content) {
        try {
            // FileWriter overwrites any existing file.
            java.io.FileWriter fileWriter = new java.io.FileWriter(file);
            // Always wrap FileWriter in BufferedWriter.
            java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a string with the current date and time.
     * @return A string with the current date and time.
     */
    public static String getCurrentDateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm aa yyyy/MM/dd");
        return sdf.format(new java.util.Date());
    }

    /**
     * Returns true if the file has not been saved.
     * @param file The file to check. Can be null
     * @param content The content of the editor
     * @return True if the file has not been saved.
     */
    public static boolean isUnsaved(File file, String content) {
        if (content == null) {
            content = "";
        }
        if (file == null && content.isEmpty()) {
            return false;
        } else if (file == null) {
            return true;
        }
        return !readFile(file).equals(content);
    }

    /**
     * Opens a new stage.
     */
    public static void newWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MainController controller = fxmlLoader.getController();
            stage.setTitle("Text Editor");
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {controller.onExit();stage.close();});
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
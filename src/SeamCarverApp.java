
import java.io.File;
import java.io.IOException;

import controller.SeamCarverController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

// main class for the seam carver app
public class SeamCarverApp extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private SeamCarverController controller;

	// starts the app
	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Seam Carver");

		Image image = this.getImage(primaryStage);

		if (image == null) {
			Platform.exit();
			return;
		}

		this.controller = new SeamCarverController(image);

		Parent scene = this.loadFXMLScene("view/SeamCarverApp.fxml", this.controller);

		primaryStage.setScene(new Scene(scene));
		primaryStage.setWidth(image.getWidth() + 200);
		primaryStage.setHeight(image.getHeight());
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(200);
		primaryStage.show();
	}

	// gets the FXML scene at the path, sets its controller to the given
	// controller
	Parent loadFXMLScene(String path, Object controller) {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(SeamCarverApp.class.getResource(path));

			loader.setController(controller);

			return loader.load();

		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}

		return null;
	}

	// gets the image for the seam carver app to act upon
	Image getImage(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Image");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg", "*.bmp"));
		File imageFile = fileChooser.showOpenDialog(stage);

		if (imageFile == null) {
			return null;
		}

		return new Image(imageFile.toURI().toString());
	}
}

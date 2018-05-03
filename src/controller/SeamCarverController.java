package controller;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.PixelBiasModel;
import model.SeamCarverModel;
import model.UndoManager;

// the controller for the seam carver app
public class SeamCarverController {

	private final UndoManager undoManager = new UndoManager();
	private final SeamCarverModel seamCarverModel;
	private final PixelBiasModel pixelBiasModel;

	@FXML
	private ScrollPane controlPanel;

	@FXML
	private TextField verticalShrinkAmountField;

	@FXML
	private TextField horizontalShrinkAmountField;

	@FXML
	private TextField verticalExpandAmountField;

	@FXML
	private TextField horizontalExpandAmountField;

	@FXML
	private TextField biasBrushSizeField;

	@FXML
	private Text originalDimensionsText;

	@FXML
	private Text carvedDimensionsText;

	@FXML
	private StackPane display;

	@FXML
	private ImageView imageView;

	@FXML
	private ImageView pixelBiasImageView;

	@FXML
	private Button undoButton;

	public SeamCarverController(Image image) {
		this.seamCarverModel = new SeamCarverModel(image, undoManager);
		this.pixelBiasModel = new PixelBiasModel(this.seamCarverModel, undoManager);
	}

	// initializes the display controller
	// EFFECT: this.imageView's fit and height properties
	@FXML
	void initialize() {

		this.undoManager.bindToNextOpName(this.undoButton.textProperty());

		this.makeNumberField(this.verticalShrinkAmountField, this.horizontalShrinkAmountField,
				this.verticalExpandAmountField, this.horizontalExpandAmountField,
				this.biasBrushSizeField);

		this.imageView.fitWidthProperty().bind(this.display.widthProperty());
		this.imageView.fitHeightProperty().bind(this.display.heightProperty());

		this.pixelBiasImageView.fitWidthProperty().bind(this.display.widthProperty());
		this.pixelBiasImageView.fitHeightProperty().bind(this.display.heightProperty());

		this.seamCarverModel.bindToCarvedImage(this.imageView.imageProperty());
		this.pixelBiasModel.bindToCanvas(this.pixelBiasImageView.imageProperty());

		this.imageView.imageProperty()
				.addListener((obs, newValue, oldValue) -> this.pixelBiasModel.makeNewCanvas());
		this.imageView.imageProperty()
				.addListener((obs, newValue, oldValue) -> this.updateStatusText());

		this.seamCarverModel.updateImage();

		this.setOriginalDimenionsText();

	}

	// expands the image horizontally by inserting horizontal artificial seams
	@FXML
	void expandHorizontally() {
		int amount = Integer.parseInt(this.horizontalExpandAmountField.getText());
		this.seamCarverModel.expandHorizontally(amount);
	}

	// expands the image vertically by inserts vertical artificial seams
	@FXML
	void expandVertically() {
		int amount = Integer.parseInt(this.verticalExpandAmountField.getText());
		this.seamCarverModel.expandVertically(amount);
	}

	// makes a bias stroke at pixels around the mouse position
	@FXML
	void drawBiasStroke(MouseEvent event) {

		Image pixelBiasImage = this.pixelBiasImageView.getImage();

		double scaleX = pixelBiasImage.getWidth() / this.pixelBiasImageView.getFitWidth();
		double scaleY = pixelBiasImage.getHeight() / this.pixelBiasImageView.getFitHeight();

		double scale = Math.max(scaleX, scaleY);

		int x = (int) (event.getX() * scale);
		int y = (int) (event.getY() * scale);

		int brushSize = Integer.parseInt(this.biasBrushSizeField.getText());

		this.pixelBiasModel.addStroke(x, y, brushSize);

	}

	// applies bias to the stroked pixels
	@FXML
	void finishBiasStroke() {
		this.pixelBiasModel.finishStroke();
	}

	// removes all bias from all pixels
	@FXML
	void removeAllBias() {
		this.pixelBiasModel.removeAllBias();
	}

	// reverts the image back to the original image
	@FXML
	void revertToOriginal() {
		this.seamCarverModel.revertToOriginal();
		this.undoManager.clear();
	}

	// saves the carved image
	@FXML
	void saveCarvedImage() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("png", "*.png"),
				new ExtensionFilter("gif", "*.gif"), new ExtensionFilter("jpg", "*.jpg", "*.jpeg"));

		File file = fileChooser.showSaveDialog(this.controlPanel.getScene().getWindow());

		if (file == null) {
			return;
		}

		Image image = this.imageView.getImage();

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null),
					fileChooser.getSelectedExtensionFilter().getDescription(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// removes horizontal seams
	@FXML
	void shrinkHorizontally() {
		int amount = Integer.parseInt(this.horizontalShrinkAmountField.getText());

		this.seamCarverModel.shrinkHorizontally(amount);

	}

	// removes vertical seams
	@FXML
	void shrinkVertically() {
		int amount = Integer.parseInt(this.verticalShrinkAmountField.getText());
		this.seamCarverModel.shrinkVertically(amount);

	}

	// undos the last operation made
	@FXML
	void undoLastOperation() {
		if (!this.undoManager.empty()) {
			this.undoManager.undoNext();
		}
	}

	// sets the original dimensions text, assumes the image has not been carved
	// yet
	private void setOriginalDimenionsText() {
		Image img = this.imageView.getImage();

		this.originalDimensionsText.setText((int) img.getWidth() + "x" + (int) img.getHeight());
	}

	// sets all the status text
	private void updateStatusText() {
		Image img = this.imageView.getImage();

		this.carvedDimensionsText.setText((int) img.getWidth() + "x" + (int) img.getHeight());
	}

	// makes the given textFields accept only numbers
	// EFFECT: textFields
	private void makeNumberField(TextField... textFields) {
		for (TextField textField : textFields) {
			textField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue,
						String newValue) {
					if (!newValue.matches("\\d*")) {
						textField.setText(newValue.replaceAll("[^\\d]", ""));
					}
				}
			});
		}
	}

}

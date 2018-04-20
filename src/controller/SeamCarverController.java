package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import model.PixelBiasModel;
import model.SeamCarverModel;

// the controller for the seam carver app
public class SeamCarverController {

	private static final String ORIGINAL_COLOR_REINSERTION_MODE = "Original Color";
	private static final String ESTIMATE_COLOR_REINSERTION_MODE = "Estimate Color";

	private final SeamCarverModel seamCarverModel;
	private final PixelBiasModel pixelBiasModel;

	@FXML
	private StackPane display;

	@FXML
	private ImageView pixelBiasImageView;

	@FXML
	private ImageView imageView;

	@FXML
	private ScrollPane controlPanel;

	@FXML
	private TextField verticalShrinkAmountField;

	@FXML
	private TextField horizontalShrinkAmountField;

	@FXML
	private TextField alternateShrinkIterationsField;

	@FXML
	private TextField randomShrinkAmountField;

	@FXML
	private TextField biasBrushSizeField;

	@FXML
	private ComboBox<String> reinsertionModeComboBox;

	@FXML
	private TextField reinsertSeamsAmountField;

	@FXML
	private Text originalDimensionsText;

	@FXML
	private Text carvedDimensionsText;

	@FXML
	private Text verticalSeamsText;

	@FXML
	private Text horizontalSeamsText;

	@FXML
	private Text totalSeamsText;

	// used for alternating between vertical and horizontal seams
	private int currentSeamOrientation = 0;

	public SeamCarverController(Image image) {
		this.seamCarverModel = new SeamCarverModel(image);
		this.pixelBiasModel = new PixelBiasModel(this.seamCarverModel);
	}

	// initializes the display controller
	// EFFECT: this.imageView's fit and height properties
	@FXML
	void initialize() {

		ObservableList<String> options = FXCollections.observableArrayList(
				ORIGINAL_COLOR_REINSERTION_MODE, ESTIMATE_COLOR_REINSERTION_MODE);

		this.reinsertionModeComboBox.setItems(options);
		this.reinsertionModeComboBox.setValue(ORIGINAL_COLOR_REINSERTION_MODE);

		this.makeNumberField(this.verticalShrinkAmountField, this.horizontalShrinkAmountField,
				this.alternateShrinkIterationsField, this.randomShrinkAmountField,
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
	void finishBiasStroke(MouseEvent event) {
		this.pixelBiasModel.finishStroke();
	}

	// reinserts all seams that have been removed from the original image
	@FXML
	void reinsertAllSeams(ActionEvent event) {
		this.reinsertSeams(this.seamCarverModel.countRemovedSeams());
	}

	// reinserts some of the seams that have been removed
	@FXML
	void reinsertSeams(ActionEvent event) {
		int amount = Integer.parseInt(this.reinsertSeamsAmountField.getText());
		this.reinsertSeams(amount);
	}

	// reinserts the given number of seams
	private void reinsertSeams(int amount) {
		boolean estimateColor =
				this.reinsertionModeComboBox.getValue().equals(ESTIMATE_COLOR_REINSERTION_MODE);

		for (int i = 0; i < amount; i++) {

			if (this.seamCarverModel.countRemovedSeams() == 0) {
				break;
			}

			this.seamCarverModel.reinsertSeam(estimateColor);
		}
	}

	// removes all bias from all pixels
	@FXML
	void removeAllBias(ActionEvent event) {
		this.pixelBiasModel.removeAllBias();
	}

	// reverts the image back to the original image
	@FXML
	void revertToOriginal(ActionEvent event) {
		this.seamCarverModel.revertToOriginal();
	}

	// alternates between horizontal and vertical seams
	@FXML
	void shrinkAlternate(ActionEvent event) {
		int amount = Integer.parseInt(this.alternateShrinkIterationsField.getText());

		for (int count = 0; count < amount; count++) {
			if (count % 2 == this.currentSeamOrientation) {
				this.shrinkHorizontally(null);
			} else {
				this.shrinkVertically(null);
			}
		}

		this.currentSeamOrientation = (this.currentSeamOrientation + amount) % 2;
	}

	// removes horizontal seams
	@FXML
	void shrinkHorizontally(ActionEvent event) {
		int amount = Integer.parseInt(this.horizontalShrinkAmountField.getText());

		for (int count = 0; count < amount; count++) {
			this.seamCarverModel.removeHorizontalSeam();
		}
	}

	// randomly removes either horizontal or vertical seams
	@FXML
	void shrinkRandomly(ActionEvent event) {
		int amount = Integer.parseInt(this.randomShrinkAmountField.getText());

		for (int count = 0; count < amount; count++) {
			if (Math.random() < 0.5) {
				this.seamCarverModel.removeHorizontalSeam();
			} else {
				this.seamCarverModel.removeVerticalSeam();
			}
		}
	}

	// removes vertical seams
	@FXML
	void shrinkVertically(ActionEvent event) {
		int amount = Integer.parseInt(this.verticalShrinkAmountField.getText());

		for (int count = 0; count < amount; count++) {
			this.seamCarverModel.removeVerticalSeam();
		}
	}

	// undos the last pixel bias stroke
	@FXML
	void undoLastBiasStroke(ActionEvent event) {
		this.pixelBiasModel.undoLastStroke();
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
		this.totalSeamsText.setText(Integer.toString(this.seamCarverModel.countRemovedSeams()));
		this.verticalSeamsText
				.setText(Integer.toString(this.seamCarverModel.countRemovedVerticalSeams()));
		this.horizontalSeamsText
				.setText(Integer.toString(this.seamCarverModel.countRemovedHorizontalSeams()));
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

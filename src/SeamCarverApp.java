import java.io.File;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import seamcarver.SeamCarver;

public class SeamCarverApp extends Application {

	static final double CONTROLS_WIDTH = 200.0;

	public static void main(String[] args) {
		launch(args);
	}

	private Image originalImage;
	private SeamCarver seamCarver;
	private ObjectProperty<Image> image;
	private StringProperty originalDimensions, dimensions, verticalSeams, horizontalSeams, totalSeams;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Seam Carver");
		primaryStage.setMinWidth(CONTROLS_WIDTH * 2);
		primaryStage.setMinHeight(CONTROLS_WIDTH);

		this.originalImage = this.getImage(primaryStage);
		this.seamCarver = new SeamCarver(this.originalImage);
		
		
		BorderPane root = new BorderPane();
		root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

		ImageView imageView = this.getImageView(primaryStage, this.originalImage);
		this.image = imageView.imageProperty();
		
		ScrollPane controls = this.getControls();
		
		this.updateProperties();

		root.setLeft(controls);
		root.setCenter(imageView);
		root.setPrefHeight(imageView.getFitHeight());

		Scene scene = new Scene(root);

		primaryStage.setScene(scene);

		primaryStage.show();

	}

	ScrollPane getControls() {
		ScrollPane controlsRoot = new ScrollPane();
		controlsRoot.setPrefWidth(CONTROLS_WIDTH);
		controlsRoot.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY, new BorderWidths(5), Insets.EMPTY)));

		controlsRoot.setFitToWidth(true);

		VBox content = new VBox();
		content.setBorder(new Border(new BorderStroke(null, null, null, null, new Insets(5))));
		content.prefWidthProperty().bind(controlsRoot.widthProperty().subtract(12));
		content.setSpacing(5);

		content.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		Text shrinkTitle = new Text("Shrink");
		shrinkTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

		Text biasTitle = new Text("Bias");
		biasTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

		Text reinsertTitle = new Text("Reinsert");
		reinsertTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		
		Text statusTitle = new Text("Status");
		statusTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

		ComboBox<String> reinsertModeComboBox = this.getReinsertModeComboBox(content);

		content.getChildren().addAll(this.getRevertToOriginalButton(content), shrinkTitle,
				this.getShrinkX(content), this.getShrinkY(content), biasTitle,
				this.getBiasBrushSize(content), this.getUndoBiasButton(content),
				this.getRemoveAllBiasButton(content), reinsertTitle, reinsertModeComboBox,
				this.getReinsert(reinsertModeComboBox, content), statusTitle, this.getStatus(content));

		controlsRoot.setContent(content);

		return controlsRoot;
	}

	Button getRevertToOriginalButton(VBox content) {
		Button button = new Button("Revert to Original");
		button.prefWidthProperty().bind(content.widthProperty());
		
		button.setOnAction((e) -> this.revertToOriginal());
		
		return button;
	}

	HBox getShrinkX(VBox content) {
		HBox container = new HBox();
		container.setSpacing(10);

		Button shrinkXButton = new Button("Shrink X");
		shrinkXButton.setPrefWidth(120);

		TextField shrinkXAmountField = new TextField();
		shrinkXAmountField.setText("1");
		shrinkXAmountField.prefWidthProperty()
				.bind(content.prefWidthProperty().subtract(shrinkXButton.prefWidthProperty()));
		this.makeNumberField(shrinkXAmountField);

		shrinkXButton.setOnAction((e) -> this
				.removeVerticalSeams(Integer.parseInt(shrinkXAmountField.textProperty().get())));

		container.getChildren().addAll(shrinkXButton, shrinkXAmountField);

		return container;
	}

	HBox getShrinkY(VBox content) {
		HBox container = new HBox();
		container.setSpacing(10);

		Button shrinkYButton = new Button("Shrink Y");
		shrinkYButton.setPrefWidth(120);

		TextField shrinkYAmountField = new TextField();
		shrinkYAmountField.setText("1");
		shrinkYAmountField.prefWidthProperty()
				.bind(content.prefWidthProperty().subtract(shrinkYButton.prefWidthProperty()));
		this.makeNumberField(shrinkYAmountField);

		shrinkYButton.setOnAction((e) -> this
				.removeHorizontalSeams(Integer.parseInt(shrinkYAmountField.textProperty().get())));

		container.getChildren().addAll(shrinkYButton, shrinkYAmountField);

		return container;
	}

	HBox getBiasBrushSize(VBox content) {
		HBox container = new HBox();
		container.setSpacing(10);

		container.setAlignment(Pos.BASELINE_CENTER);

		Text biasBrushSizeText = new Text("Bias Brush Size");
		biasBrushSizeText.setTextAlignment(TextAlignment.CENTER);

		TextField biasBrushSizeField = new TextField();
		biasBrushSizeField.setText("5");
		biasBrushSizeField.prefWidthProperty().bind(content.prefWidthProperty()
				.subtract(biasBrushSizeText.getBoundsInLocal().getWidth()));
		this.makeNumberField(biasBrushSizeField);

		container.getChildren().addAll(biasBrushSizeText, biasBrushSizeField);

		return container;

	}

	Button getUndoBiasButton(VBox content) {
		Button button = new Button("Undo Bias");
		button.prefWidthProperty().bind(content.widthProperty());
		return button;
	}

	Button getRemoveAllBiasButton(VBox content) {
		Button button = new Button("Remove All Bias");
		button.prefWidthProperty().bind(content.widthProperty());
		return button;
	}

	ComboBox<String> getReinsertModeComboBox(VBox content) {
		ObservableList<String> options =
				FXCollections.observableArrayList("Use Original Color", "Estimate Color");

		ComboBox<String> comboBox = new ComboBox<String>(options);
		comboBox.setValue("Use Original Color");
		comboBox.prefWidthProperty().bind(content.prefWidthProperty());

		return comboBox;
	}

	HBox getReinsert(ComboBox<String> mode, VBox content) {
		HBox container = new HBox();
		container.setSpacing(10);

		Button reinsertButton = new Button("Reinsert");
		reinsertButton.setPrefWidth(120);

		TextField reinsertAmountField = new TextField();
		reinsertAmountField.setText("1");
		reinsertAmountField.prefWidthProperty()
				.bind(content.prefWidthProperty().subtract(reinsertButton.prefWidthProperty()));
		this.makeNumberField(reinsertAmountField);

		reinsertButton
				.setOnAction((e) -> this.reinsertSeams(mode.getValue().equals("Estimate Color"),
						Integer.parseInt(reinsertAmountField.textProperty().get())));

		container.getChildren().addAll(reinsertButton, reinsertAmountField);

		return container;
	}
	
	VBox getStatus(VBox content) {
		VBox status = new VBox();
		status.setSpacing(5);

		Text originalDimensionsText = new Text();
		Text dimensionsText = new Text();
		Text verticalSeams = new Text();
		Text horizontalSeams = new Text();
		Text totalSeams = new Text();
		
		this.originalDimensions = originalDimensionsText.textProperty();
		this.dimensions = dimensionsText.textProperty();
		this.verticalSeams = verticalSeams.textProperty();
		this.horizontalSeams = horizontalSeams.textProperty();
		this.totalSeams = totalSeams.textProperty();
		
		status.prefWidthProperty().bind(content.prefWidthProperty());
		
		status.getChildren().addAll(originalDimensionsText, dimensionsText, verticalSeams, horizontalSeams, totalSeams);
		
		return status;
	}

	void makeNumberField(TextField textField) {
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

	ImageView getImageView(Stage stage, Image image) {
		ImageView imageView = new ImageView(image);

		imageView.setPreserveRatio(true);

		imageView.fitWidthProperty().bind(stage.widthProperty().subtract(CONTROLS_WIDTH).add(-30));
		imageView.fitHeightProperty().bind(stage.heightProperty().add(-30));

		return imageView;
	}

	Image getImage(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Image");
		fileChooser.getExtensionFilters()
				.add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
		File imageFile = fileChooser.showOpenDialog(stage);
		return new Image(imageFile.toURI().toString());
	}

	void removeVerticalSeams(int amount) {
		for (int i = 0; i < amount; i += 1) {
			
			if (this.seamCarver.getWidth() == 0) {
				break;
			}
			
			this.seamCarver.removeVerticalSeam();
		}
		this.image.set(this.seamCarver.makeImage());
		this.updateProperties();
	}

	void removeHorizontalSeams(int amount) {
		for (int i = 0; i < amount; i += 1) {
			
			if (this.seamCarver.getHeight() == 0) {
				break;
			}
			
			this.seamCarver.removeHorizontalSeam();
		}
		this.image.set(this.seamCarver.makeImage());
		this.updateProperties();
	}

	void reinsertSeams(boolean estimateColor, int amount) {
		for (int i = 0; i < amount; i += 1) {
			
			if (this.seamCarver.countRemovedSeams() == 0) {
				break;
			}
			
			this.seamCarver.reinsertSeam(estimateColor);
		}
		this.image.set(this.seamCarver.makeImage());
		this.updateProperties();
		
	}
	
	void revertToOriginal() {
		this.image.set(this.originalImage);
		this.seamCarver = new SeamCarver(this.originalImage);
		this.updateProperties();
	}
	
	void updateProperties() {
		this.originalDimensions.set("Original: " + (int) this.originalImage.getWidth() + "x" + (int) this.originalImage.getHeight());
		this.dimensions.set("Carved: " + this.seamCarver.getWidth() + "x" + this.seamCarver.getHeight());
		
		this.verticalSeams.set("Vertical Seams: " + (int) (this.originalImage.getWidth() - this.seamCarver.getWidth()));
		this.horizontalSeams.set("Horizontal Seams: " + (int) (this.originalImage.getHeight() - this.seamCarver.getHeight()));
		
		this.totalSeams.set("Total Seams: " + this.seamCarver.countRemovedSeams());
	}

}

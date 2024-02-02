package lk.playtech.controller;

import com.jfoenix.controls.JFXButton;
import com.vdurmont.emoji.Emoji;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.playtech.emoji.EmojiPicker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientController {
    @FXML
    private AnchorPane pane;

    @FXML
    private JFXButton btnSend;

    @FXML
    private ImageView imjSend;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField txtMessege;

    @FXML
    private VBox vBox;

    @FXML
    private JFXButton btnEmoji;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3002);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");

                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receiveMessage(receivingMsg,ClientController.this.vBox);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
        emoji();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        sendMsg(txtMessege.getText());
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if (!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 0, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);


                try {
                    Stage primary = (Stage) btnSend.getScene().getWindow();
                    String clientName = primary.getTitle();
                    dataOutputStream.writeUTF(clientName + "-" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtMessege.clear();
            }
        }
    }

    public void receiveMessage(String msg, VBox vBox) throws IOException {
        if (msg.matches(".*\\.(png|jpe?g|gif)$")) {
            Stage stage = (Stage) btnSend.getScene().getWindow();
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(stage.getTitle());
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Image image = new Image(msg);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 5, 5, 10));
            hBox.getChildren().add(imageView);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_LEFT);
            hBoxTime.setPadding(new Insets(0, 5, 5, 10));
            String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text time = new Text(stringTime);
            time.setStyle("-fx-font-size: 8");

            hBoxTime.getChildren().add(time);

            Platform.runLater(() -> {
                vBox.getChildren().add(hBoxName);
                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);
            });
        } else {
            String name = msg.split("-")[0];
            String msgFromServer = msg.split("-")[1];

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Text text = new Text(msgFromServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    HBox hBoxTime = new HBox();
                    hBoxTime.setAlignment(Pos.CENTER_LEFT);
                    hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                    String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    Text time = new Text(stringTime);
                    time.setStyle("-fx-font-size: 8");

                    hBoxTime.getChildren().add(time);

                    vBox.getChildren().add(hBoxName);
                    vBox.getChildren().add(hBox);
                    vBox.getChildren().add(hBoxTime);
                }
            });
        }
    }

    @FXML
    void btnImageBrowserOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        Stage stage = (Stage) btnSend.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String sendImage = file.toURI().toString();
            System.out.println("Image URL: " + sendImage);
            sendImageToServer(sendImage);
        }
    }

    private void sendImageToServer(String sendImage) {
        Image image = new Image(sendImage);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        HBox hBoxTime = new HBox();
        hBoxTime.setAlignment(Pos.CENTER_RIGHT);
        hBoxTime.setPadding(new Insets(0, 5, 5, 10));
        String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Text time = new Text(stringTime);
        time.setStyle("-fx-font-size: 8");

        hBoxTime.getChildren().add(time);

        vBox.getChildren().add(hBox);
        vBox.getChildren().add(hBoxTime);

        try {
            dataOutputStream.writeUTF(sendImage);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void emoji() {
        EmojiPicker emojiPicker = new EmojiPicker();
        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(75,300);
        vBox.setLayoutX(40);
        vBox.setLayoutY(60);
        //vBox.setStyle("-fx-font-size: 30");

        pane.getChildren().add(vBox);
        emojiPicker.setVisible(false);

        btnEmoji.setOnAction(MouseEvent ->{
            if(emojiPicker.isVisible()){
                emojiPicker.setVisible(false);
            }else{
                emojiPicker.setVisible(true);
            }
        });

        emojiPicker.getEmojiListView().setOnMouseClicked(mouseEvent -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();

            if (selectedEmoji != null){
                txtMessege.setText(txtMessege.getText() + selectedEmoji);
            }
            emojiPicker.setVisible(false);
        });
    }
}
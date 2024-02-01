package lk.playtech.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private JFXButton btnJoin;

    @FXML
    private JFXTextField txtUserName;
    private String [] userNames = {"pathum", "lahiru", "chandana"};

    @FXML
    void btnJoinOnAction(ActionEvent event) throws IOException {
        for (int i = 0; i < userNames.length; i++) {
            if(txtUserName.getText().equals(userNames[i])) {
                txtUserName.clear();
                AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/client_form.fxml"));
                Scene scene = new Scene(rootNode);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle(userNames[i]);
                stage.centerOnScreen();
                stage.show();
                return;
            }
        }
        new Alert(Alert.AlertType.ERROR,"Invalid input").show();
    }
}

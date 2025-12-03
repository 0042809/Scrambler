package uz.rayimbek;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScramblerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label kLabel = new Label("k (binary):");
        TextField kField = new TextField("11011011");

        Label sLabel = new Label("s (binary):");
        TextField sField = new TextField("11001011");

        Label cipherLabel = new Label("Cipher (hex):");
        TextField cipherField = new TextField();

        Label plaintextLabel = new Label("Plaintext:");
        TextField plaintextField = new TextField();

        Button decodeBtn = new Button("Decode → Plaintext");
        Button encodeBtn = new Button("Encode → Hex");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        decodeBtn.setOnAction(e -> {
            try {
                int k = Integer.parseInt(kField.getText(), 2);
                int s = Integer.parseInt(sField.getText(), 2);
                int key = k ^ s;

                byte[] cipherBytes = hexToBytes(cipherField.getText());
                StringBuilder result = new StringBuilder();
                for (byte b : cipherBytes) {
                    result.append((char) ((b ^ key) & 0xFF));
                }

                output.setText("Key = " + key + " (0x" + Integer.toHexString(key) + ")\n"
                        + "Decoded text: " + result);
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });

        encodeBtn.setOnAction(e -> {
            try {
                int k = Integer.parseInt(kField.getText(), 2);
                int s = Integer.parseInt(sField.getText(), 2);
                int key = k ^ s;

                String text = plaintextField.getText();
                StringBuilder hex = new StringBuilder();

                for (char ch : text.toCharArray()) {
                    int enc = ((int) ch) ^ key;
                    hex.append(String.format("%02X", enc));
                }

                output.setText("Key = " + key + " (0x" + Integer.toHexString(key) + ")\n"
                        + "Encoded hex: " + hex);
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });

        VBox root = new VBox(10,
                kLabel, kField,
                sLabel, sField,
                cipherLabel, cipherField,
                plaintextLabel, plaintextField,
                decodeBtn, encodeBtn,
                output
        );
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Scrambler Cipher (XOR)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s+", "");
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return data;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

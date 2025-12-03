package uz.rayimbek;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class ScramblerApp extends Application {

    private TextField kField;
    private TextField sField;
    private TextField cField;

    private TextArea resultArea;
    private TextArea processArea;

    @Override
    public void start(Stage primaryStage) {
        kField = new TextField();
        sField = new TextField();
        cField = new TextField();

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        inputGrid.addRow(0, new Label("K (1-Blok, Binar 8bit):"), kField);
        inputGrid.addRow(1, new Label("S (Keyingi, Binar 8bit):"), sField);
        inputGrid.addRow(2, new Label("C (Shifrlangan, Hex):"), cField);

        Button decryptBtn = new Button("Deshifrlashni Boshlash (C → P)");
        decryptBtn.setOnAction(e -> handleDecrypt());

        HBox buttonBox = new HBox(20, decryptBtn);
        buttonBox.setAlignment(Pos.CENTER);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(3);

        processArea = new TextArea();
        processArea.setEditable(false);
        processArea.setWrapText(true);
        processArea.setPrefHeight(300);

        VBox root = new VBox(10,
                new Label("XOR Deshifrlash (K/S Qoidasi)"),
                inputGrid,
                buttonBox,
                new Separator(),
                new Label("Natija:"),
                resultArea,
                new Label("Jarayon Tafsilotlari:"),
                processArea
        );
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 500, 650);
        primaryStage.setTitle("Scrambler K/S XOR Ilovasi");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private byte[] hexToBytes(String hex) throws IllegalArgumentException {
        hex = hex.replaceAll("\\s+", "").toUpperCase();
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex matnning uzunligi juft bo'lishi kerak.");
        }
        Pattern hexPattern = Pattern.compile("^[0-9A-F]+$");
        if (!hexPattern.matcher(hex).matches()) {
            throw new IllegalArgumentException("Hex matn faqat 0-9 va A-F harflaridan iborat bo'lishi kerak.");
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return data;
    }

    private byte binToByte(String bin) throws IllegalArgumentException {
        if (bin.length() != 8 || !bin.matches("[01]+")) {
            throw new IllegalArgumentException("Kalit (K yoki S) aniq 8 bitli binar (0/1) bo'lishi kerak.");
        }
        return (byte) Integer.parseInt(bin, 2);
    }

    private void handleDecrypt() {
        processArea.clear();
        resultArea.clear();

        try {
            byte kKey = binToByte(kField.getText().trim());
            byte sKey = binToByte(sField.getText().trim());

            String cHexInput = cField.getText().trim();
            if (cHexInput.isEmpty()) {
                resultArea.setText("Xatolik: Shifrlangan matn (C) kiritilmagan.");
                return;
            }
            byte[] cBytes = hexToBytes(cHexInput);

            StringBuilder decryptedText = new StringBuilder();

            processArea.appendText("=== Deshifrlash (C → P) ===\n");

            for (int i = 0; i < cBytes.length; i++) {
                byte cByte = cBytes[i];
                byte currentKey;

                currentKey = (i == 0) ? kKey : sKey;
                String keyName = (i == 0) ? "K" : "S";

                byte decryptedByte = (byte) (cByte ^ currentKey);
                decryptedText.append(new String(new byte[]{decryptedByte}, StandardCharsets.US_ASCII));

                String cHexBlock = String.format("%02X", cByte);
                String cBinBlock = String.format("%8s", Integer.toBinaryString(cByte & 0xFF)).replace(' ', '0');
                String keyBinBlock = String.format("%8s", Integer.toBinaryString(currentKey & 0xFF)).replace(' ', '0');
                String decryptedBinBlock = String.format("%8s", Integer.toBinaryString(decryptedByte & 0xFF)).replace(' ', '0');

                processArea.appendText(String.format("--- Blok %d (%s) ---\n", i + 1, cHexBlock));
                processArea.appendText("  Shifrlangan (C): " + cBinBlock + "\n");
                processArea.appendText("  Kalit (%s):        %s\n");
                processArea.appendText("  Natija (P):      " + decryptedBinBlock + " ('" + new String(new byte[]{decryptedByte}, StandardCharsets.US_ASCII) + "')\n");
            }

            resultArea.setText("DESHIFRLANGAN MATN: " + decryptedText.toString());

        } catch (IllegalArgumentException e) {
            resultArea.setText("Xatolik: " + e.getMessage());
        } catch (Exception e) {
            resultArea.setText("Noma'lum xato: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package uz.rayimbek;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ScramblerApp extends Application {

    private TextArea inputArea;
    private TextArea outputArea;
    private TextField keyField;
    private ComboBox<String> algorithmBox;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scrambler Shifrlash Dasturi");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        VBox topBox = createHeader();
        root.setTop(topBox);

        VBox centerBox = createCenterContent();
        root.setCenter(centerBox);

        HBox bottomBox = createBottomBar();
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label title = new Label("🔐 SCRAMBLER SHIFRLASH TIZIMI");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Matnlarni xavfsiz shifrlang va deshifrlang");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#e0e0e0"));

        box.getChildren().addAll(title, subtitle);
        return box;
    }

    private VBox createCenterContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        HBox settingsBox = createSettingsPanel();

        HBox textBox = createTextAreas();

        HBox buttonBox = createButtons();

        box.getChildren().addAll(settingsBox, textBox, buttonBox);
        return box;
    }

    private HBox createSettingsPanel() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 10;");

        Label algLabel = new Label("Algoritm:");
        algLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll(
                "Caesar Cipher",
                "Reverse Scrambler",
                "Block Scrambler",
                "Rail Fence Cipher",
                "Columnar Transposition"
        );
        algorithmBox.setValue("Block Scrambler");
        algorithmBox.setPrefWidth(200);

        Label keyLabel = new Label("Kalit so'z:");
        keyLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        keyField = new TextField();
        keyField.setPromptText("Kalitni kiriting...");
        keyField.setPrefWidth(200);

        box.getChildren().addAll(algLabel, algorithmBox, new Separator(), keyLabel, keyField);
        return box;
    }

    private HBox createTextAreas() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefHeight(350);

        VBox inputBox = new VBox(8);
        Label inputLabel = new Label("📝 Asl Matn");
        inputLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        inputLabel.setTextFill(Color.WHITE);

        inputArea = new TextArea();
        inputArea.setPromptText("Shifrlanadigan matnni kiriting...");
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(300);
        inputArea.setStyle("-fx-font-size: 13px;");

        inputBox.getChildren().addAll(inputLabel, inputArea);
        inputBox.setMaxWidth(400);

        VBox outputBox = new VBox(8);
        Label outputLabel = new Label("🔒 Shifrlangan Matn");
        outputLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        outputLabel.setTextFill(Color.WHITE);

        outputArea = new TextArea();
        outputArea.setPromptText("Natija shu yerda ko'rsatiladi...");
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(300);
        outputArea.setEditable(false);
        outputArea.setStyle("-fx-font-size: 13px; -fx-control-inner-background: #f5f5f5;");

        outputBox.getChildren().addAll(outputLabel, outputArea);
        outputBox.setMaxWidth(400);

        box.getChildren().addAll(inputBox, outputBox);
        return box;
    }

    private HBox createButtons() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        Button encryptBtn = createStyledButton("🔐 Shifrlash", "#4CAF50");
        encryptBtn.setOnAction(e -> encrypt());

        Button decryptBtn = createStyledButton("🔓 Deshifrlash", "#2196F3");
        decryptBtn.setOnAction(e -> decrypt());

        Button clearBtn = createStyledButton("🗑️ Tozalash", "#FF5722");
        clearBtn.setOnAction(e -> clear());

        Button copyBtn = createStyledButton("📋 Nusxa Olish", "#9C27B0");
        copyBtn.setOnAction(e -> copyToClipboard());

        Button loadBtn = createStyledButton("📂 Fayldan Yuklash", "#FF9800");
        loadBtn.setOnAction(e -> loadFromFile());

        Button saveBtn = createStyledButton("💾 Saqlash", "#00BCD4");
        saveBtn.setOnAction(e -> saveToFile());

        box.getChildren().addAll(encryptBtn, decryptBtn, clearBtn, copyBtn, loadBtn, saveBtn);
        return box;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 12px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 5; " +
                        "-fx-cursor: hand;", color
        ));
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-opacity: 0.8;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.8;", "")));
        return btn;
    }

    private HBox createBottomBar() {
        HBox box = new HBox();
        box.setPadding(new Insets(10, 20, 10, 20));
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        statusLabel = new Label("✅ Tayyor");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("System", 12));

        box.getChildren().add(statusLabel);
        return box;
    }

    private void encrypt() {
        String input = inputArea.getText();
        String key = keyField.getText();
        String algorithm = algorithmBox.getValue();

        if (input.isEmpty()) {
            showStatus("❌ Matn kiritilmagan!", true);
            return;
        }

        try {
            String result = switch (algorithm) {
                case "Caesar Cipher" -> caesarCipher(input, getKeyValue(key), true);
                case "Reverse Scrambler" -> reverseScrambler(input);
                case "Block Scrambler" -> blockScrambler(input, key);
                case "Rail Fence Cipher" -> railFenceCipher(input, getKeyValue(key), true);
                case "Columnar Transposition" -> columnarTransposition(input, key, true);
                default -> "";
            };
            outputArea.setText(result);
            showStatus("✅ Shifrlash muvaffaqiyatli!", false);
        } catch (Exception e) {
            showStatus("❌ Xatolik: " + e.getMessage(), true);
        }
    }

    private void decrypt() {
        String input = inputArea.getText();
        String key = keyField.getText();
        String algorithm = algorithmBox.getValue();

        if (input.isEmpty()) {
            showStatus("❌ Matn kiritilmagan!", true);
            return;
        }

        try {
            String result = switch (algorithm) {
                case "Caesar Cipher" -> caesarCipher(input, getKeyValue(key), false);
                case "Reverse Scrambler" -> reverseScrambler(input);
                case "Block Scrambler" -> blockDescrambler(input, key);
                case "Rail Fence Cipher" -> railFenceCipher(input, getKeyValue(key), false);
                case "Columnar Transposition" -> columnarTransposition(input, key, false);
                default -> "";
            };
            outputArea.setText(result);
            showStatus("✅ Deshifrlash muvaffaqiyatli!", false);
        } catch (Exception e) {
            showStatus("❌ Xatolik: " + e.getMessage(), true);
        }
    }

    private String caesarCipher(String text, int shift, boolean encrypt) {
        if (!encrypt) shift = -shift;
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) ((c - base + shift + 26) % 26 + base);
            }
            result.append(c);
        }
        return result.toString();
    }

    private String reverseScrambler(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    private String blockScrambler(String text, String key) {
        if (key.isEmpty()) key = "SECRET";
        Random rand = new Random(key.hashCode());
        List<Character> chars = new ArrayList<>();
        for (char c : text.toCharArray()) chars.add(c);
        Collections.shuffle(chars, rand);
        StringBuilder result = new StringBuilder();
        for (char c : chars) result.append(c);
        return result.toString();
    }

    private String blockDescrambler(String text, String key) {
        if (key.isEmpty()) key = "SECRET";
        Random rand = new Random(key.hashCode());
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) indices.add(i);
        Collections.shuffle(indices, rand);

        char[] result = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            result[indices.get(i)] = text.charAt(i);
        }
        return new String(result);
    }

    private String railFenceCipher(String text, int rails, boolean encrypt) {
        if (rails <= 1) rails = 3;
        if (encrypt) {
            StringBuilder[] fence = new StringBuilder[rails];
            for (int i = 0; i < rails; i++) fence[i] = new StringBuilder();

            int row = 0, direction = 1;
            for (char c : text.toCharArray()) {
                fence[row].append(c);
                row += direction;
                if (row == 0 || row == rails - 1) direction = -direction;
            }

            StringBuilder result = new StringBuilder();
            for (StringBuilder sb : fence) result.append(sb);
            return result.toString();
        } else {
            int[] railLengths = new int[rails];
            int row = 0, direction = 1;
            for (int i = 0; i < text.length(); i++) {
                railLengths[row]++;
                row += direction;
                if (row == 0 || row == rails - 1) direction = -direction;
            }

            char[][] fence = new char[rails][];
            int index = 0;
            for (int i = 0; i < rails; i++) {
                fence[i] = new char[railLengths[i]];
                for (int j = 0; j < railLengths[i]; j++) {
                    fence[i][j] = text.charAt(index++);
                }
            }

            StringBuilder result = new StringBuilder();
            row = 0;
            direction = 1;
            int[] positions = new int[rails];
            for (int i = 0; i < text.length(); i++) {
                result.append(fence[row][positions[row]++]);
                row += direction;
                if (row == 0 || row == rails - 1) direction = -direction;
            }
            return result.toString();
        }
    }

    private String columnarTransposition(String text, String finalKey1, boolean encrypt) {
        if (finalKey1.isEmpty()) finalKey1 = "KEY";
        finalKey1 = finalKey1.toUpperCase();

        int cols = finalKey1.length();
        int rows = (int) Math.ceil((double) text.length() / cols);
        char[][] grid = new char[rows][cols];
        if (encrypt) {

            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = index < text.length() ? text.charAt(index++) : 'X';
                }
            }

            Integer[] order = new Integer[cols];
            for (int i = 0; i < cols; i++) order[i] = i;
            Arrays.sort(order, Comparator.comparing(finalKey1::charAt));

            StringBuilder result = new StringBuilder();
            for (int col : order) {
                for (int row = 0; row < rows; row++) {
                    result.append(grid[row][col]);
                }
            }
            return result.toString();
        } else {

            Integer[] order = new Integer[cols];
            for (int i = 0; i < cols; i++) order[i] = i;
            Arrays.sort(order, Comparator.comparing(finalKey1::charAt));

            int index = 0;
            for (int col : order) {
                for (int row = 0; row < rows; row++) {
                    grid[row][col] = index < text.length() ? text.charAt(index++) : 'X';
                }
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.append(grid[i][j]);
                }
            }
            return result.toString().replace("X", "");
        }
    }

    private int getKeyValue(String key) {
        if (key.isEmpty()) return 3;
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return Math.abs(key.hashCode() % 26) + 1;
        }
    }


    private void clear() {
        inputArea.clear();
        outputArea.clear();
        keyField.clear();
        showStatus("✅ Tozalandi", false);
    }

    private void copyToClipboard() {
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
            showStatus("✅ Nusxa olindi!", false);
        } else {
            showStatus("❌ Nusxa olinadigan matn yo'q!", true);
        }
    }

    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Faylni tanlang");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                inputArea.setText(content);
                showStatus("✅ Fayl yuklandi: " + file.getName(), false);
            } catch (IOException e) {
                showStatus("❌ Faylni o'qishda xatolik!", true);
            }
        }
    }

    private void saveToFile() {
        String text = outputArea.getText();
        if (text.isEmpty()) {
            showStatus("❌ Saqlanadigan matn yo'q!", true);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Saqlash");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Files.write(file.toPath(), text.getBytes());
                showStatus("✅ Fayl saqlandi: " + file.getName(), false);
            } catch (IOException e) {
                showStatus("❌ Faylni saqlashda xatolik!", true);
            }
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.web("#ffcdd2") : Color.web("#c8e6c9"));
    }
}
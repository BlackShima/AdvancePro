package se233.chapter3.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MainViewController {
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private ListView<String> inputListView;
    @FXML private Button startButton;
    @FXML private TextArea fileInfoArea;
    @FXML private ComboBox<String> formatComboBox;
    @FXML private ComboBox<String> qualityComboBox;
    @FXML private ComboBox<Integer> sampleRateComboBox;
    @FXML private ComboBox<String> channelsComboBox;

    private final DoubleProperty totalProgress = new SimpleDoubleProperty(0);
    private final Map<String, List<String>> qualityOptions = new HashMap<>();
    private final Map<String, List<Integer>> sampleRateOptions = new HashMap<>();
    private final Map<String, List<String>> channelOptions = new HashMap<>();

    @FXML
    public void initialize() {
        progressBar.progressProperty().bind(totalProgress);
        setupOptions();
        setupComboBoxes();
        setupDragAndDrop();
        setupButtonAction();
        setupFileSelectionListener();
    }

    // NEW HELPER METHOD FOR SHOWING ERROR DIALOGS
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void startMultithreadedConversion(List<String> files, String outputPath) {
        Task<Long> preScanTask = new Task<>() {
            @Override
            protected Long call() throws Exception {
                updateMessage("Scanning files...");
                long totalDurationNs = 0;
                FFprobe ffprobe = new FFprobe();
                for (String path : files) {
                    FFmpegProbeResult result = ffprobe.probe(path);
                    totalDurationNs += result.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
                }
                return totalDurationNs;
            }
        };

        // UPDATED: Proper error handling for the pre-scan stage
        preScanTask.setOnFailed(e -> {
            Throwable ex = preScanTask.getException();
            String errorMsg = "Could not read metadata from input files. Please ensure FFprobe is installed correctly and the files are valid.";
            if (ex.getMessage().contains("Cannot run program \"ffprobe\"")) {
                errorMsg = "Critical Error: ffprobe executable not found. Please install FFmpeg and ensure it's in your system's PATH.";
            }
            showErrorAlert("Error Reading Files", errorMsg);
            startButton.setDisable(false);
        });

        preScanTask.setOnSucceeded(e -> {
            long totalDurationNs = preScanTask.getValue();
            if (totalDurationNs > 0) {
                runConversionTasks(files, outputPath, totalDurationNs);
            } else {
                showErrorAlert("No Valid Files", "Could not find any valid audio streams in the selected files.");
                startButton.setDisable(false);
            }
        });

        progressLabel.textProperty().bind(preScanTask.messageProperty());
        new Thread(preScanTask).start();
    }

    private void runConversionTasks(List<String> files, String outputPath, long totalDurationNs) {
        startButton.setDisable(true);
        progressLabel.textProperty().unbind();
        progressLabel.setText("Converting...");

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        AtomicLong cumulativeProgress = new AtomicLong(0);
        List<Task<Void>> conversionTasks = new ArrayList<>();

        for (String inputPath : files) {
            String format = formatComboBox.getValue();
            String quality = qualityComboBox.getValue();
            Integer sampleRate = sampleRateComboBox.getValue();
            String channels = channelsComboBox.getValue();

            Task<Void> task = new ConversionTask(inputPath, outputPath, format, quality, sampleRate, channels,
                    totalProgress, totalDurationNs, cumulativeProgress);

            task.setOnSucceeded(evt -> fileInfoArea.appendText("\nConverted: " + new File(inputPath).getName()));

            // UPDATED: Proper error handling for each conversion task
            task.setOnFailed(evt -> {
                Throwable ex = task.getException();
                String errorMsg;
                if (ex instanceof ConversionException) {
                    // ถ้าใช่, แสดงข้อความจาก Exception ของเรา
                    errorMsg = "Could not convert " + new File(inputPath).getName() + ".\n\nDetails: " + ex.getMessage();

                    // 2. การตรวจสอบเรื่อง ffmpeg ยังคงมีประโยชน์ ควรเก็บไว้
                } else if (ex.getMessage() != null && ex.getMessage().contains("Cannot run program \"ffmpeg\"")) {
                    errorMsg = "Critical Error: ffmpeg executable not found. Please install FFmpeg and ensure it's in your system's PATH.";

                    // 3. กรณีเป็น Exception อื่นๆ ที่ไม่คาดคิด
                } else {
                    errorMsg = "An unexpected error occurred with " + new File(inputPath).getName() + ".";
                }

                showErrorAlert("Conversion Failed", errorMsg);
                fileInfoArea.appendText("\nFailed: " + new File(inputPath).getName());
            });

            conversionTasks.add(task);
            executor.submit(task);
        }

        Task<Void> monitorTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (Task<Void> task : conversionTasks) {
                    try {
                        task.get(); // Wait for each task to complete
                    } catch (Exception e) {
                        // Ignore here, handled by individual OnFailed handlers
                    }
                }
                return null;
            }
        };

        monitorTask.setOnSucceeded(e -> {
            progressLabel.setText("All conversions complete!");
            totalProgress.set(1.0);
            startButton.setDisable(false);
            executor.shutdown();
        });

        new Thread(monitorTask).start();
    }

    private void displayFileMetadata(String filePath) {
        Task<String> metadataTask = new Task<>() {
            // ... call() method is unchanged
            @Override
            protected String call() throws Exception {
                final FFprobe ffprobe = new FFprobe();
                FFmpegProbeResult probeResult = ffprobe.probe(filePath);

                FFmpegFormat format = probeResult.getFormat();
                FFmpegStream stream = probeResult.getStreams().get(0);

                String channels = stream.channel_layout != null ? stream.channel_layout : String.valueOf(stream.channels);

                return String.format(
                        "File: %s\n" +
                                "Format: %s\n" +
                                "Duration: %.2f seconds\n\n" +
                                "Codec: %s\n" +
                                "Sample Rate: %d Hz\n" +
                                "Channels: %s\n" +
                                "Bit Rate: %d kb/s",
                        new File(filePath).getName(),
                        format.format_long_name,
                        format.duration,
                        stream.codec_long_name,
                        stream.sample_rate,
                        channels.substring(0, 1).toUpperCase() + channels.substring(1),
                        (stream.bit_rate / 1000)
                );
            }
        };

        metadataTask.setOnSucceeded(event -> fileInfoArea.setText(metadataTask.getValue()));

        // UPDATED: Better error message in the text area (no pop-up needed)
        metadataTask.setOnFailed(event -> {
            Throwable ex = metadataTask.getException();
            fileInfoArea.setText("Failed to read file metadata.\n\nPlease ensure the file is not corrupted and ffprobe is installed correctly.\n\nDetails: " + ex.getMessage());
        });

        new Thread(metadataTask).start();
    }

    // All other methods are unchanged...
    private void setupOptions() {
        // Quality Options
        qualityOptions.put("MP3", Arrays.asList("128 kbps", "192 kbps", "320 kbps"));
        qualityOptions.put("WAV", Arrays.asList("16-bit PCM", "24-bit PCM"));
        qualityOptions.put("M4A", Arrays.asList("128 kbps", "192 kbps", "256 kbps"));
        qualityOptions.put("FLAC", Arrays.asList("Level 5 (Default)", "Level 8 (Max)"));

        // Sample Rate Options
        List<Integer> standardSampleRates = Arrays.asList(44100, 48000, 88200, 96000);
        sampleRateOptions.put("MP3", standardSampleRates);
        sampleRateOptions.put("WAV", standardSampleRates);
        sampleRateOptions.put("M4A", standardSampleRates);
        sampleRateOptions.put("FLAC", standardSampleRates);

        // Channel Options
        List<String> standardChannels = Arrays.asList("Stereo", "Mono");
        channelOptions.put("MP3", standardChannels);
        channelOptions.put("WAV", standardChannels);
        channelOptions.put("M4A", standardChannels);
        channelOptions.put("FLAC", standardChannels);
    }

    private void setupComboBoxes() {
        formatComboBox.getItems().addAll("MP3", "WAV", "M4A", "FLAC");
        formatComboBox.setValue("MP3");

        formatComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateComboBoxes(newVal);
            }
        });
        updateComboBoxes("MP3");
    }

    private void updateComboBoxes(String format) {
        qualityComboBox.setItems(FXCollections.observableArrayList(qualityOptions.get(format)));
        qualityComboBox.setValue(qualityOptions.get(format).get(0));

        sampleRateComboBox.setItems(FXCollections.observableArrayList(sampleRateOptions.get(format)));
        sampleRateComboBox.setValue(sampleRateOptions.get(format).get(0));

        channelsComboBox.setItems(FXCollections.observableArrayList(channelOptions.get(format)));
        channelsComboBox.setValue(channelOptions.get(format).get(0));
    }

    private void setupDragAndDrop() {
        inputListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                boolean allFilesAreAudio = db.getFiles().stream().allMatch(f -> f.getName().toLowerCase().matches(".*\\.(mp3|wav|m4a|flac)$"));
                if (allFilesAreAudio) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        inputListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                inputListView.getItems().addAll(db.getFiles().stream().map(File::getAbsolutePath).collect(Collectors.toList()));
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    private void setupButtonAction() {
        startButton.setOnAction(event -> {
            if (inputListView.getItems().isEmpty()) {
                fileInfoArea.setText("Error: No input files selected.");
                return;
            }
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Folder");
            File outputDir = directoryChooser.showDialog(startButton.getScene().getWindow());
            if (outputDir == null) {
                fileInfoArea.setText("Error: No output directory selected.");
                return;
            }

            List<String> filesToConvert = new ArrayList<>(inputListView.getItems());
            inputListView.getItems().clear();

            startMultithreadedConversion(filesToConvert, outputDir.getAbsolutePath());
        });
    }

    private void setupFileSelectionListener() {
        inputListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayFileMetadata(newSelection);
            }
        });
    }
}
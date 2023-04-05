package com.example.mp3musicplayer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class musicPlayerController implements Initializable {
    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private Image image;
    @FXML
    private ImageView iconLabel;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Media media;
    private MediaPlayer mediaPlayer;
    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private String path;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<File>();
        directory = new File("src/musicFiles");
        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                songs.add(file);
                System.out.println(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());

        for (int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
        }
        speedBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
            }
        });
        songProgressBar.setStyle("-fx-accent: #00FF00;");
    }
    public void openFileMethod(ActionEvent event){
        FileChooser fileChooser= new FileChooser();
        File file= fileChooser.showOpenDialog(null);
        path = file.toURI().toString();

        if(path!=null){
            Media media= new Media(path);
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(file.getName());
            image= new Image(getClass().getResourceAsStream("/images/playIcon1.png"));
            iconLabel.setImage(image);
            beginTimer();
            changeSpeed(null);
            mediaPlayer.play();
        }
    }
    public void addPlaylist(){
        FileChooser fileChooser= new FileChooser();
        File file= fileChooser.showOpenDialog(null);
        path = file.toURI().toString();

        if(path!=null){
            Media media= new Media(path);
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(file.getName());
            image= new Image(getClass().getResourceAsStream("/images/playIcon1.png"));
            iconLabel.setImage(image);
            songs.add(file);
            beginTimer();
            changeSpeed(null);
            mediaPlayer.play();
        }
    }
    public void exitMethod(){
        Platform.exit();
        System.exit(0);
    }
    public void openAbout(ActionEvent event){
        final String msg= "MP3 Music Player \n"+
                "This software is developed by \n" +
                "Anganba Singha (MUH2001049M)\n" +
                "Towsif Bin Zaman (MUH2001035M)";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.setTitle("About MP3 Music Player");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void playMedia() {
        beginTimer();
        changeSpeed(null);
        mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
        image= new Image(getClass().getResourceAsStream("/images/playIcon1.png"));
        iconLabel.setImage(image);
        mediaPlayer.play();
    }
    public void pauseMedia() {
        cancelTimer();
        image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
        iconLabel.setImage(image);
        mediaPlayer.pause();
    }
    public void resetMedia() {
        songProgressBar.setProgress(0);
        image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
        iconLabel.setImage(image);
        mediaPlayer.seek(Duration.seconds(0));
    }
    public void previousMedia() {
        if (songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
            iconLabel.setImage(image);
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
        } else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
            iconLabel.setImage(image);
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
        }
    }
    public void nextMedia() {
        if (songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
            iconLabel.setImage(image);
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
        } else {
            songNumber = 0;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            image= new Image(getClass().getResourceAsStream("/images/defaultIcon.png"));
            iconLabel.setImage(image);
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
        }
    }
    public void forwardSkip(ActionEvent event){
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }
    public void backwardSkip(ActionEvent event){
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
    }
    public void changeSpeed(ActionEvent event) {
        // mediaPlayer.setRate(Integer.parseInt(speedBox.getValue())*0.01);
        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }
    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running=true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);
                if(current/end==1){
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task,0,1000);
    }
    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

}

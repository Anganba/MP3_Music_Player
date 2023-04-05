module com.example.mp3musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
            
                            
    opens com.example.mp3musicplayer to javafx.fxml;
    exports com.example.mp3musicplayer;
}
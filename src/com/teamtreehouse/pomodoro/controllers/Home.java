package com.teamtreehouse.pomodoro.controllers;

import com.teamtreehouse.pomodoro.model.Attempt;
import com.teamtreehouse.pomodoro.model.AttemptKind;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class Home {

    private final AudioClip mBell;
    @FXML
    private VBox container;
    @FXML
    private Label title;
    @FXML
    private TextArea message;

    private Attempt mCurrentAttempt;
    private StringProperty mTimerText;  // pozwala na wysłanie sygnału z tego kontrolera do
                                        // faktycznej aplikacji o tym, że zaszła zmiana
    private Timeline mTimeLine;

    public Home() {
        mTimerText = new SimpleStringProperty();
        setTimerText(25*60);
        mBell = new AudioClip(getClass().getResource("/sounds/bell.mp3").toExternalForm());
    }

    public String getTimerText() {
        return mTimerText.get();
    }

    public StringProperty timerTextProperty() {
        return mTimerText;
    }

    public void setTimerText(String mTimerText) {
        this.mTimerText.set(mTimerText);
    }

    public void setTimerText(int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        setTimerText(String.format("%02d:%02d", minutes, seconds));
    }

    private void prepareAttempt(AttemptKind kind) {
        reset();
        mCurrentAttempt = new Attempt(kind, "");
        addAttemptStyle(kind);
        title.setText(kind.getDisplayName());
        setTimerText(mCurrentAttempt.getRemainingSeconds());

        mTimeLine = new Timeline();
        mTimeLine.setCycleCount(kind.getTotalSeconds());

        mTimeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            mCurrentAttempt.tick();
            setTimerText(mCurrentAttempt.getRemainingSeconds());
        }));

        mTimeLine.setOnFinished(e -> {
            saveCurrentAttempt();
            mBell.play();
            prepareAttempt(mCurrentAttempt.getKind() == AttemptKind.FOCUS ?
            AttemptKind.BREAK : AttemptKind.FOCUS);
        }); // Jeżeli obecnym stanem jest FOCUS, to (?) if przekazuje BREAK. Jeżeli obecnym stanem
            // nie jest FOCUS, to (:) otherwise ustawiamy obecny stan na FOCUS
    }

    private void saveCurrentAttempt() {
        // mock method
        mCurrentAttempt.setMessage(message.getText());
        mCurrentAttempt.save();
    }

    private void reset() {
        clearAttemptStyles();
        if (mTimeLine != null && mTimeLine.getStatus() == Animation.Status.RUNNING) {
            mTimeLine.stop();
        }
    }

    public void playTimer() {
        container.getStyleClass().add("playing");
        mTimeLine.play();
    }

    public void pauseTimer() {
        container.getStyleClass().remove("playing");
        mTimeLine.pause();
    }

    private void clearAttemptStyles() {
        container.getStyleClass().remove("playing");
        for (AttemptKind kind : AttemptKind.values()) {
            container.getStyleClass().remove(kind.toString().toLowerCase());
        }
    }

    private void addAttemptStyle(AttemptKind kind) {
        container.getStyleClass().add(kind.toString().toLowerCase());
    }

    public void handleRestart(ActionEvent actionEvent) {
        prepareAttempt(AttemptKind.FOCUS);
        playTimer();
    }

    public void handlePlay(ActionEvent actionEvent) {
        if (mCurrentAttempt == null) {
            handleRestart(actionEvent);
        } else {
            playTimer();
        }
    }

    public void handlePause(ActionEvent actionEvent) {
        pauseTimer();
    }
}

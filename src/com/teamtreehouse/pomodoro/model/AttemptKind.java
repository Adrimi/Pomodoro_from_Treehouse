package com.teamtreehouse.pomodoro.model;

public enum AttemptKind {
    FOCUS(25 * 60, "Focus time"), BREAK(5 * 60, "Break time");
    //FOCUS(3, "Focus time"), BREAK(10, "Break time");

    private int mTotalSeconds;
    private String mDisplayName;

    public int getTotalSeconds() {
        return mTotalSeconds;
    }

    AttemptKind(int totalSeconds, String displayName) {
        mTotalSeconds = totalSeconds;
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}

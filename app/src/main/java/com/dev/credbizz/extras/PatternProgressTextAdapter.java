package com.dev.credbizz.extras;

import androidx.annotation.NonNull;

public class PatternProgressTextAdapter implements CircularProgressIndicator.ProgressTextAdapter {

    private String pattern;

    public PatternProgressTextAdapter(String pattern) {
        this.pattern = pattern;
    }

    @NonNull
    @Override
    public String formatText(double currentProgress) {
        return String.format(pattern, currentProgress);
    }
}

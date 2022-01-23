package com.homepanel.core.state;

public class State<T> {

    private T inputFormat;
    private String outputFormat;

    public T getInputFormat() {
        return inputFormat;
    }

    private void setInputFormat(T inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    private void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public State(T inputFormat, String outputFormat) {
        setInputFormat(inputFormat);
        setOutputFormat(outputFormat);
    }
}
package com.homepanel.core.storage;


import java.io.*;

public class Storage {
/*
    private final static Logger LOGGER = LoggerFactory.getLogger(Storage.class);

    private JSONObject data;
    private String filePath;

    public JSONObject getData() {
        return data;
    }

    private void setData(JSONObject data) {
        this.data = data;
    }

    private String getFilePath() {
        return filePath;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Storage(String propertiesPath) {
        init(propertiesPath);
    }

    private void init(String filePath) {

        setData(new JSONObject());

        setFilePath(filePath);

        if (new File(getFilePath()).exists()) {
            try {
                try (FileReader fileReader = new FileReader(getFilePath())) {
                    try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line =  bufferedReader.readLine()) != null){
                            sb.append(line);
                        }
                        try {
                            setData(new JSONObject(sb.toString()));
                        } catch (Exception e) {
                            LOGGER.error("can not convert storage file \"" + getFilePath() + "\" to json object", e);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("storage file \"" + getFilePath() + "\" not found", e);
            } catch (IOException e) {
                LOGGER.error("can not load storage file \"" + getFilePath() + "\"", e);
            }
        }
    }

    public void save() {

        try {
            try (FileWriter fileWriter = new FileWriter(getFilePath())) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    bufferedWriter.write(getData().toString(2));
                }
            }
        } catch (IOException e) {
            LOGGER.error("can not save storage file \"" + getFilePath() + "\"", e);
        }
    }

    public static ZonedDateTime getLocalDateTime(Long value) {

        if (value != null) {
            return DateTime.getLocalDateTime(value);
        }

        return null;
    }

    public static Long getLong(ZonedDateTime value) {

        if (value != null) {
            return DateTime.getTimeInMilliseconds(value);
        }

        return null;
    }*/
}
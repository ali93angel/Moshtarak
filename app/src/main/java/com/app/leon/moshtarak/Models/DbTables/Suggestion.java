package com.app.leon.moshtarak.Models.DbTables;

public class Suggestion {
    String type;
    String message;
    String osVersion;
    String phoneModel;

    public Suggestion(String type, String message, String osVersion, String phoneModel) {
        this.type = type;
        this.message = message;
        this.osVersion = osVersion;
        this.phoneModel = phoneModel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }
}

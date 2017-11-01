package com.dcdineshk.googlemap.model;

public class ImageUploadInfo {

    public String userName;
    public String userEmail;

    public String imageURL;
    public String uuid;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url, String userEmail,String uuid) {

        this.userName = name;
        this.userEmail = userEmail;
        this.imageURL= url;
        this.uuid= uuid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUuid() {
        return uuid;
    }

}
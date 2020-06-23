package com.rafi_studio.familycontact;

public class ContactModel {

    private int id;
    private String image;
    private String english_mobile_no;
    private String bangla_mobile_no;
    private String englishName;
    private String banglaName;
    private String designation;
    private String email;

    public ContactModel(int id, String image, String english_mobile_no, String bangla_mobile_no, String englishName, String banglaName, String designation, String email) {
        this.id = id;
        this.image = image;
        this.english_mobile_no = english_mobile_no;
        this.bangla_mobile_no = bangla_mobile_no;
        this.englishName = englishName;
        this.banglaName = banglaName;
        this.designation = designation;
        this.email = email;
    }

    public ContactModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEnglish_mobile_no() {
        return english_mobile_no;
    }

    public void setEnglish_mobile_no(String english_mobile_no) {
        this.english_mobile_no = english_mobile_no;
    }

    public String getBangla_mobile_no() {
        return bangla_mobile_no;
    }

    public void setBangla_mobile_no(String bangla_mobile_no) {
        this.bangla_mobile_no = bangla_mobile_no;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getBanglaName() {
        return banglaName;
    }

    public void setBanglaName(String banglaName) {
        this.banglaName = banglaName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

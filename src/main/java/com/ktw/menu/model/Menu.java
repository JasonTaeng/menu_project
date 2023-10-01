package com.ktw.menu.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Menu {
    private String name;
    private String oldLocation;
    private String newLocation;
    private String kakaoGrade;
    private String kakaoReviewNum;
    private String naverGrade;
    private String naverReviewNum;
    private String googleGrade;
    private String googleReviewNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(String oldLocation) {
        this.oldLocation = oldLocation;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    public String getKakaoGrade() {
        return kakaoGrade;
    }

    public void setKakaoGrade(String kakaoGrade) {
        this.kakaoGrade = kakaoGrade;
    }

    public String getKakaoReviewNum() {
        return kakaoReviewNum;
    }

    public void setKakaoReviewNum(String kakaoReviewNum) {
        this.kakaoReviewNum = kakaoReviewNum;
    }

    public String getNaverGrade() {
        return naverGrade;
    }

    public void setNaverGrade(String naverGrade) {
        this.naverGrade = naverGrade;
    }

    public String getNaverReviewNum() {
        return naverReviewNum;
    }

    public void setNaverReviewNum(String naverReviewNum) {
        this.naverReviewNum = naverReviewNum;
    }

    public String getGoogleGrade() {
        return googleGrade;
    }

    public void setGoogleGrade(String googleGrade) {
        this.googleGrade = googleGrade;
    }

    public String getGoogleReviewNum() {
        return googleReviewNum;
    }

    public void setGoogleReviewNum(String googleReviewNum) {
        this.googleReviewNum = googleReviewNum;
    }

}

package com.ktw.menu.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MenuRank {

    private int rank;
    private float avgGrade;
    private int totalReviewNum;
    private String name;
    private String newLoc;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getAvgGrade() {
        return avgGrade;
    }

    public void setAvgGrade(float avgGrade) {
        this.avgGrade = avgGrade;
    }

    public int getTotalReviewNum() {
        return totalReviewNum;
    }

    public void setTotalReviewNum(int totalReviewNum) {
        this.totalReviewNum = totalReviewNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewLoc() {
        return newLoc;
    }

    public void setNewLoc(String newLoc) {
        this.newLoc = newLoc;
    }
}

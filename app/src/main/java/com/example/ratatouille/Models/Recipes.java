package com.example.ratatouille.Models;

import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class Recipes {

    private String recipeId ;

    private String recipeName;

    private String recipeImageUrl=null;

    private int cookTimeMin;

    private List<String> ingredients;

    private List<String>moods;

    private String recipeDescription;

    private String chefId;

    private String chefName;

    private long timeStamp;

    private int noOfLikes=0;

    private boolean isVeg;

    private int healthy;

    //Should be enum !!
    private String region;

    public Recipes(){}

    public Recipes(String recipeName, String recipeDescription, String chefId, String chefName,long timeStamp,List<String> ingredient,List<String>moods,int cookTimeMin,boolean isVeg,int healthy,String region) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.chefId = chefId;
        this.chefName=chefName;
        this.timeStamp = timeStamp;
        this.ingredients=ingredient;
        this.moods=moods;
        this.cookTimeMin=cookTimeMin;
        this.isVeg=isVeg;
        this.healthy=healthy;
        this.region=region;
    }

    public Recipes(String recipeName, String recipeDescription, String chefId, String chefName,long timeStamp,List<String> ingredient,List<String>moods,int cookTimeMin) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.chefId = chefId;
        this.chefName=chefName;
        this.timeStamp = timeStamp;
        this.ingredients=ingredient;
        this.moods=moods;
        this.cookTimeMin=cookTimeMin;
        this.isVeg=isVeg;
        this.healthy=healthy;
        this.region=region;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public String getChefId() {
        return chefId;
    }

    public void setChefId(String chefId) {
        this.chefId = chefId;
    }

    public String getChefName() {
        return chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }


    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void increamentLike(){
        this.noOfLikes++;
    }

    public void disLike(){
        this.noOfLikes--;
    }

    public int getCookTimeMin() {
        return cookTimeMin;
    }

    public void setCookTimeMin(int cookTimeMin) {
        this.cookTimeMin = cookTimeMin;
    }

    public List<String> getMoods() {
        return moods;
    }

    public void setMoods(List<String> moods) {
        this.moods = moods;
    }


    public boolean isVeg() {
        return isVeg;
    }

    public void setVeg(boolean veg) {
        isVeg = veg;
    }

    public int getHealthy() {
        return healthy;
    }

    public void setHealthy(int healthy) {
        this.healthy = healthy;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

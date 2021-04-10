package com.example.ratatouille.Models;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Recipes implements Serializable {

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


    public Recipes(QueryDocumentSnapshot document) {

        this.recipeId = document.getString("recipeId");
        this.recipeName = document.getString("recipeName");
        this.recipeDescription = document.getString("recipeDescription");
//        this.diet = document.getString("diet");
        this.isVeg = document.getBoolean("veg");
        this.recipeImageUrl = document.getString("recipeImageUrl");
        this.chefId = document.getString("chefId");
        this.chefName = document.getString("chefName");
        this.timeStamp = (long) document.get("timeStamp");
        this.ingredients = (List<String>) document.get("ingredients");
        this.moods = (List<String>) document.get("moods");
        this.cookTimeMin = ((Long) document.get("cookTimeMin")).intValue();
        this.noOfLikes = ((Long) document.get("noOfLikes")).intValue();
//        this.noOfFavourites = ((Long) document.get("noOfFavourites")).intValue();
        this.region = (String) document.get("region");

//        Log.d(TAG,  " recipeId " + document.getString("recipeId") + "recipeName = " + document.getString("recipeName") );
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

    @Override
    public String toString() {
        return "Recipes{" +
                "recipeId='" + recipeId + '\'' +
                ", recipeName='" + recipeName + '\'' +
                ", recipeImageUrl='" + recipeImageUrl + '\'' +
                ", cookTimeMin=" + cookTimeMin +
                ", ingredients=" + ingredients +
                ", moods=" + moods +
                ", recipeDescription='" + recipeDescription + '\'' +
                ", chefId='" + chefId + '\'' +
                ", chefName='" + chefName + '\'' +
                ", timeStamp=" + timeStamp +
                ", noOfLikes=" + noOfLikes +
                ", isVeg=" + isVeg +
                ", healthy=" + healthy +
                ", region='" + region + '\'' +
                '}';
    }

    public static class RecipeCustomSortingComparator implements Comparator<Recipes> {

        private User userDetails;

        public void setUserDetails(User userDetails) {
            this.userDetails = userDetails;
        }

        @Override
        public int compare(Recipes recipe1, Recipes recipe2) {

            //comparing "vegetarian" and "non-vegetarian"

            int val1=0, val2=0;
            if(recipe1.isVeg() == userDetails.isVeg()) {
                val1-=500;
            }
            if(recipe2.isVeg() == userDetails.isVeg()) {
                val2+=500;
            }

            if(userDetails.getCooking() != 0){
                int v = userDetails.getCooking();
                val1+=((recipe1.cookTimeMin/v)*(recipe1.cookTimeMin/v));
                val2+=((recipe2.cookTimeMin/v)*(recipe2.cookTimeMin/v));
            } else {
                val1+=((recipe1.cookTimeMin*2)*(recipe1.cookTimeMin*2));
                val2+=((recipe2.cookTimeMin*2)*(recipe2.cookTimeMin*2));
            }

            int ff = userDetails.getFastfood();
            if(ff < 5) {
                if(recipe1.getMoods().contains("fast food")) {
                    val1+=(5-ff)*50;
                }
                if(recipe1.getMoods().contains("fast food")) {
                    val2+=(5-ff)*50;
                }
            }

            // using no. of likes ; actually should be ((recipe1.noOfLikes/totalUsers)*100)*5;
            val1-=(recipe1.noOfLikes)*10;
            val2-=(recipe2.noOfLikes)*10;

            //comparing region of recipes
            if(recipe1.getRegion() != null){
                if((recipe1.getRegion()).equalsIgnoreCase(userDetails.getReg_food())){
                    val1-=200;
                }
                if((recipe2.getRegion()).equalsIgnoreCase(userDetails.getReg_food())){
                    val1-=200;
                }
            }

            if(val1 <= val2){
                return -1;
            } else if(val1 > val2){
                return 1;
            }
            return 0;
        }
    }

    public String toShareString() {
        return "Hey! checkout this Recipe by "+chefName+": \n\n" +
                recipeName.toUpperCase() + "\n\n" +
                "Cooking Time : " + cookTimeMin + "( mins)\n"+
                "Ingredients required are " + ingredients +"\n\n"+
                recipeDescription +"\n\n" +
                "isVeg : " + isVeg + "\n"+
                "healthy : " + healthy +"/10\n\n"+
                recipeImageUrl + '\n';
    }
}

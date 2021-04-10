package com.example.ratatouille.Activity;

//class for Survey Answers at Onboarding Screen

public class SurveyAnswer {

    private String  sp_sw_sr = "", reg_food = "" ;
    private int health, fastfood, newfood, cooking;
    private boolean veg = true;

    //getters and setters

    public String getSp_sw_sr() {
        return sp_sw_sr;
    }

    public String getReg_food() {
        return reg_food;
    }

    public int getHealth() {
        return health;
    }

    public int getFastfood() {
        return fastfood;
    }

    public int getNewfood() {
        return newfood;
    }

    public int getCooking() {
        return cooking;
    }

    public void setSp_sw_sr(String sp_sw_sr) {
        this.sp_sw_sr = sp_sw_sr;
    }

    public void setReg_food(String reg_food) {
        this.reg_food = reg_food;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setFastfood(int fastfood) {
        this.fastfood = fastfood;
    }

    public void setNewfood(int newfood) {
        this.newfood = newfood;
    }

    public void setCooking(int cooking) {
        this.cooking = cooking;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    //constructors for SurveyAnswer class

    SurveyAnswer(){

    }

    SurveyAnswer(String veg_nonveg, String sp_sw_sr, String reg_food, int health, int fastfood, int newfood, int cooking){
        this.sp_sw_sr = sp_sw_sr.toLowerCase();
        this.reg_food = reg_food.toLowerCase();
        this.health = health;
        this.fastfood = fastfood;
        this.newfood = newfood;
        this.cooking = cooking;
        if(veg_nonveg.equalsIgnoreCase("vegetarian")){
            this.veg = true;
        } else {
            this.veg = false;
        }
    }

}

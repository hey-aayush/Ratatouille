package com.example.ratatouille.Activity;

//class for Survey Answers at Onboarding Screen

public class SurveyAnswer {

    private String veg_nonveg = "", sp_sw_sr = "", reg_food = "" ;
    private int health, fastfood, newfood, cooking;

    //getters and setters

    public String getVeg_nonveg() {
        return veg_nonveg;
    }

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

    public void setVeg_nonveg(String veg_nonveg) {
        this.veg_nonveg = veg_nonveg;
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

    //constructors for SurveyAnswer class

    SurveyAnswer(){

    }

    SurveyAnswer(String veg_nonveg, String sp_sw_sr, String reg_food, int health, int fastfood, int newfood, int cooking){
        this.veg_nonveg = veg_nonveg;
        this.sp_sw_sr = sp_sw_sr;
        this.reg_food = reg_food;
        this.health = health;
        this.fastfood = fastfood;
        this.newfood = newfood;
        this.cooking = cooking;
    }
}

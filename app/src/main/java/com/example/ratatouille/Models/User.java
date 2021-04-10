package com.example.ratatouille.Models;

import com.example.ratatouille.Activity.SurveyAnswer;

public class User {

    private String name,uid, email, reg_food, sp_sw_sr;
    private boolean veg = true;
    private int cooking, fastfood, health,newfood;

    public User(String name,String uid,String email){
        this.name=name;
        this.uid=uid;
        this.email=email;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNewfood() {
        return newfood;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setNewfood(int newfood) {
        this.newfood = newfood;
    }

    public int getFastfood() {
        return fastfood;
    }

    public void setFastfood(int fastfood) {
        this.fastfood = fastfood;
    }

    public int getCooking() {
        return cooking;
    }

    public void setCooking(int cooking) {
        this.cooking = cooking;
    }

    public String getReg_food() {
        return reg_food;
    }

    public void setReg_food(String reg_food) {
        this.reg_food = reg_food;
    }

    public String getSp_sw_sr() {
        return sp_sw_sr;
    }

    public void setSp_sw_sr(String sp_sw_sr) {
        this.sp_sw_sr = sp_sw_sr;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public void setUserSurvey(SurveyAnswer answer) {
        this.sp_sw_sr = answer.getSp_sw_sr();
        this.reg_food = answer.getReg_food();
        this.health = answer.getHealth();
        this.fastfood = answer.getFastfood();
        this.newfood = answer.getNewfood();
        this.cooking = answer.getCooking();
        this.veg = answer.isVeg();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", reg_food='" + reg_food + '\'' +
                ", sp_sw_sr='" + sp_sw_sr + '\'' +
                ", veg=" + veg +
                ", cooking=" + cooking +
                ", fastfood=" + fastfood +
                ", health=" + health +
                ", newfood=" + newfood +
                '}';
    }
}

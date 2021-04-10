package com.example.ratatouille.Models;

public class Likes {

    public int reactions;

    public Likes(){};

    public Likes(int reactions){
        this.reactions=reactions;
    };

    public int getReactions() {
        return reactions;
    }

    public void setReactions(int reactions) {
        this.reactions = reactions;
    }
}

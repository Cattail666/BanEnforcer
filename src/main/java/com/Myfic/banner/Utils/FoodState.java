package com.Myfic.banner.Utils;


public class FoodState {

    public int beforeFoodLeve;
    public float beforeSaturation;
    public FoodState foodState;
    public FoodState(){

    }
    public void setBeforeFoodLeve(int value){
         this.beforeFoodLeve = value;
    }
    public void setBeforeSaturation(float value){
        this.beforeSaturation = value;
    }
    public void setRecord(int foodlevel, float saturation){
        setBeforeFoodLeve(foodlevel);
        setBeforeSaturation(saturation);
    }
    public static FoodState getInstance(){
        return new FoodState();
    }
}

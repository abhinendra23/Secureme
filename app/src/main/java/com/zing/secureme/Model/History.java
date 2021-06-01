package com.zing.secureme.Model;

public class History implements Cloneable {

    String PlaceName,duration,numofpeople,score;

    public History(){

    }

    public History(String placeName, String duration, String numofpeople, String score) {
        PlaceName = placeName;
        this.duration = duration;
        this.numofpeople = numofpeople;
        this.score = score;

    }



    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNumofpeople() {
        return numofpeople;
    }

    public void setNumofpeople(String numofpeople) {
        this.numofpeople = numofpeople;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

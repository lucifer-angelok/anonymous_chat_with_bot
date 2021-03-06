package org.lucifer.abchat.dto;


import org.lucifer.abchat.config.AppGlobal;

public class AppGlobalDTO {
    private double botProbability = AppGlobal.getBotProbability();
    private int pointsWin = AppGlobal.getPointsWin();
    private int pointsLoose = AppGlobal.getPointsLoose();
    private boolean addBotAfter = AppGlobal.isAddBotAfter();
    private int retries = AppGlobal.getRetries();

    public AppGlobalDTO() {

    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public double getBotProbability() {
        return botProbability;
    }

    public void setBotProbability(double botProbability) {
        this.botProbability = botProbability;
    }

    public int getPointsWin() {
        return pointsWin;
    }

    public void setPointsWin(int pointsWin) {
        this.pointsWin = pointsWin;
    }

    public int getPointsLoose() {
        return pointsLoose;
    }

    public void setPointsLoose(int pointsLoose) {
        this.pointsLoose = pointsLoose;
    }

    public boolean isAddBotAfter() {
        return addBotAfter;
    }

    public void setAddBotAfter(boolean addBotAfter) {
        this.addBotAfter = addBotAfter;
    }
}

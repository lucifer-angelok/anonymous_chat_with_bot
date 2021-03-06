package org.lucifer.abchat.config;


public class AppGlobal {
    private static double botProbability = 0.5;
    private static int pointsWin = 10;
    private static int pointsLoose = 10;
    private static boolean addBotAfter = false;
    private static int retries = 15;

    public static int getRetries() {
        return retries;
    }

    public static void setRetries(int retries) {
        AppGlobal.retries = retries;
    }

    public static double getBotProbability() {
        return botProbability;
    }

    public static void setBotProbability(double botProbability) {
        AppGlobal.botProbability = botProbability;
    }

    public static int getPointsWin() {
        return pointsWin;
    }

    public static void setPointsWin(int pointsWin) {
        AppGlobal.pointsWin = pointsWin;
    }

    public static int getPointsLoose() {
        return pointsLoose;
    }

    public static void setPointsLoose(int pointsLoose) {
        AppGlobal.pointsLoose = pointsLoose;
    }

    public static boolean isAddBotAfter() {
        return addBotAfter;
    }

    public static void setAddBotAfter(boolean addBotAfter) {
        AppGlobal.addBotAfter = addBotAfter;
    }
}

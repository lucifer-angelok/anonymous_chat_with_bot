package org.lucifer.abchat.dto;

import java.util.Date;

public class StatisticsDTO {
    private Date startDate;
    private Date endDate;
    private Long usersCount;
    private Long registered;
    private Long userActivity;
    private Long messagesCount;
    private Integer correctPercentage;
    private Integer fooledByBotPercentage;
    private Long sizeOfCrowdSource;
    private Long newCrowdSourceRecords;
    private Long botChatsCount;
    private Long chatCount;

    public StatisticsDTO() {

    }

    public Long getBotChatsCount() {
        return botChatsCount;
    }

    public void setBotChatsCount(Long botChatsCount) {
        this.botChatsCount = botChatsCount;
    }

    public Long getChatCount() {
        return chatCount;
    }

    public void setChatCount(Long chatCount) {
        this.chatCount = chatCount;
    }

    public Long getNewCrowdSourceRecords() {
        return newCrowdSourceRecords;
    }

    public void setNewCrowdSourceRecords(Long newCrowdSourcerecords) {
        this.newCrowdSourceRecords = newCrowdSourcerecords;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Long usersCount) {
        this.usersCount = usersCount;
    }

    public Long getRegistered() {
        return registered;
    }

    public void setRegistered(Long registered) {
        this.registered = registered;
    }

    public Long getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(Long userActivity) {
        this.userActivity = userActivity;
    }

    public Long getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(Long messagesCount) {
        this.messagesCount = messagesCount;
    }

    public Integer getCorrectPercentage() {
        return correctPercentage;
    }

    public void setCorrectPercentage(Integer correctPercentage) {
        this.correctPercentage = correctPercentage;
    }

    public Integer getFooledByBotPercentage() {
        return fooledByBotPercentage;
    }

    public void setFooledByBotPercentage(Integer fooledByBotPercentage) {
        this.fooledByBotPercentage = fooledByBotPercentage;
    }

    public Long getSizeOfCrowdSource() {
        return sizeOfCrowdSource;
    }

    public void setSizeOfCrowdSource(Long sizeOfCrowdSource) {
        this.sizeOfCrowdSource = sizeOfCrowdSource;
    }
}

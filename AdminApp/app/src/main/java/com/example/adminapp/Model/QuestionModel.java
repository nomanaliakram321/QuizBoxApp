package com.example.adminapp.Model;

public class QuestionModel {
    private  String quetionId;
    private  String quetion;
    private  String optionA;
    private  String optionB;
    private  String optionC;
    private  String optionD;
    private  int correctAnswer;

    public QuestionModel(String quetionId, String quetion, String optionA, String optionB, String optionC, String optionD, int correctAnswer) {
        this.quetionId = quetionId;
        this.quetion = quetion;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public String getQuetionId() {
        return quetionId;
    }

    public void setQuetionId(String quetionId) {
        this.quetionId = quetionId;
    }

    public String getQuetion() {
        return quetion;
    }

    public void setQuetion(String quetion) {
        this.quetion = quetion;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}

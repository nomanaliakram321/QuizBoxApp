package com.example.adminapp.Model;

public class CategoryModel {
    private String id;
    private String name;
    private String noOfSets;
    private String setCounter;

    public CategoryModel(String id, String name, String noOfSets,String setCounter) {
        this.id = id;
        this.name = name;
        this.noOfSets = noOfSets;
        this.setCounter=setCounter;
    }

    public String getSetCounter() {
        return setCounter;
    }

    public void setSetCounter(String setCounter) {
        this.setCounter = setCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoOfSets() {
        return noOfSets;
    }

    public void setNoOfSets(String noOfSets) {
        this.noOfSets = noOfSets;
    }
}

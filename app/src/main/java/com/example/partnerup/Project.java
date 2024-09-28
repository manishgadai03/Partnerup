package com.example.partnerup;

import java.util.List;

public class Project {
    private String id;
    private String title;
    private String description;
    private String teamSize;
    private String location;
    private String deadline;
    private String projectType;
    private String collegeName;
    private List<String> selectedSkills;

    // Default constructor required for Firestore
    public Project() {}

    public Project(String id, String title, String description, String teamSize, String location, String deadline, String projectType, String collegeName, List<String> selectedSkills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.teamSize = teamSize;
        this.location = location;
        this.deadline = deadline;
        this.projectType = projectType;
        this.collegeName = collegeName;
        this.selectedSkills = selectedSkills;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTeamSize() { return teamSize; }
    public String getLocation() { return location; }
    public String getDeadline() { return deadline; }
    public String getProjectType() { return projectType; }
    public String getCollegeName() { return collegeName; }
    public List<String> getSelectedSkills() { return selectedSkills; }
}

package com.example.partnerup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class formFragment extends Fragment {

    private Spinner skillsSpinner, projectTypeSpinner;
    private ChipGroup selectedSkillsChipGroup;
    private List<String> selectedSkills;
    private EditText collegeNameEditText, teamSizeEditText, locationEditText, deadlineEditText, projectTitleEditText, projectDescriptionEditText;
    private Button selectDeadlineButton, submitButton;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        skillsSpinner = view.findViewById(R.id.skillsSpinner);
        selectedSkillsChipGroup = view.findViewById(R.id.selectedSkillsChipGroup);
        selectedSkills = new ArrayList<>();
        projectTypeSpinner = view.findViewById(R.id.projectTypeSpinner);
        collegeNameEditText = view.findViewById(R.id.collegeNameEditText);
        teamSizeEditText = view.findViewById(R.id.teamSizeEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        deadlineEditText = view.findViewById(R.id.deadlineEditText);
        projectTitleEditText = view.findViewById(R.id.projectTitle);
        projectDescriptionEditText = view.findViewById(R.id.projectDescription);
        selectDeadlineButton = view.findViewById(R.id.selectDeadlineButton);
        submitButton = view.findViewById(R.id.submitButton);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup Spinners
        setupSkillsSpinner();
        setupProjectTypeSpinner();

        // Setup button listeners
        selectDeadlineButton.setOnClickListener(v -> showDatePickerDialog());
        submitButton.setOnClickListener(v -> saveProjectData());

        return view;
    }

    private void setupSkillsSpinner() {
        String[] skills = {"Java", "Kotlin", "Firebase", "Android", "UI/UX", "Python"};
        ArrayAdapter<String> skillsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, skills);
        skillsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillsSpinner.setAdapter(skillsAdapter);

        skillsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSkill = (String) parent.getItemAtPosition(position);
                if (!selectedSkills.contains(selectedSkill)) {
                    selectedSkills.add(selectedSkill);
                    addSkillChip(selectedSkill);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupProjectTypeSpinner() {
        ArrayAdapter<CharSequence> projectTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.project_types, android.R.layout.simple_spinner_item);
        projectTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectTypeSpinner.setAdapter(projectTypeAdapter);

        projectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                collegeNameEditText.setVisibility(selectedType.equals("College Project") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                collegeNameEditText.setVisibility(View.GONE);
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            deadlineEditText.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void addSkillChip(String skill) {
        Chip chip = new Chip(getActivity());
        chip.setText(skill);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedSkills.remove(skill);
            selectedSkillsChipGroup.removeView(chip);
        });
        selectedSkillsChipGroup.addView(chip);
    }

    private void saveProjectData() {
        String projectTitle = projectTitleEditText.getText().toString().trim();
        String projectDescription = projectDescriptionEditText.getText().toString().trim();
        String teamSize = teamSizeEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String deadline = deadlineEditText.getText().toString().trim();
        String projectType = projectTypeSpinner.getSelectedItem().toString();
        String collegeName = projectType.equals("College Project") ? collegeNameEditText.getText().toString().trim() : "";

        // Validate input fields
        if (projectTitle.isEmpty() || projectDescription.isEmpty() || teamSize.isEmpty() || location.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to hold the project data
        Map<String, Object> projectData = new HashMap<>();
        projectData.put("title", projectTitle);
        projectData.put("description", projectDescription);
        projectData.put("teamSize", teamSize);
        projectData.put("location", location);
        projectData.put("deadline", deadline);
        projectData.put("projectType", projectType);
        projectData.put("collegeName", collegeName);
        projectData.put("selectedSkills", selectedSkills); // Store the list of selected skills

        // Use add() to create a new document with a unique ID
        db.collection("projects")
                .add(projectData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Project saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Project saved with ID: " + documentReference.getId());
                    clearFields(); // Optionally clear fields after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to save project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error saving project", e);
                });
    }

    // Optionally clear input fields after saving
    private void clearFields() {
        projectTitleEditText.setText("");
        projectDescriptionEditText.setText("");
        teamSizeEditText.setText("");
        locationEditText.setText("");
        deadlineEditText.setText("");
        collegeNameEditText.setText("");
        selectedSkills.clear();
        selectedSkillsChipGroup.removeAllViews();
    }
}

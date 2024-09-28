package com.example.partnerup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the list and adapter
        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(projectAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch projects
        fetchProjects();

        // Find the button in the layout
        Button openFormButton = view.findViewById(R.id.openFormButton);

        // Set an onClickListener to open the form fragment
        openFormButton.setOnClickListener(v -> openFormFragment());

        return view;
    }

    private void fetchProjects() {
        db.collection("projects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project project = document.toObject(Project.class);
                            projectList.add(new Project(document.getId(), project.getTitle(), project.getDescription(), project.getTeamSize(), project.getLocation(), project.getDeadline(), project.getProjectType(), project.getCollegeName(), project.getSelectedSkills()));
                        }
                        projectAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to open FormFragment
    private void openFormFragment() {
        Fragment formFragment = new formFragment();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, formFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

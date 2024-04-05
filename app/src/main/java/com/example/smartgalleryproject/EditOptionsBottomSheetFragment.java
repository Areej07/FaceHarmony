package com.example.smartgalleryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditOptionsBottomSheetFragment extends BottomSheetDialogFragment {

    private String selectedPhotoPath;

    // Factory method to create a new instance of the bottom sheet
    public static EditOptionsBottomSheetFragment newInstance(String selectedPhotoPath) {
        EditOptionsBottomSheetFragment fragment = new EditOptionsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("selectedPhotoPath", selectedPhotoPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedPhotoPath = getArguments().getString("selectedPhotoPath");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_options_bottom_sheet, container, false);

        // Example: Handle editing logic (replace with your actual editing functionality)
        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the edit button click here
                // You might want to start a new activity for editing the photo
                // or show a dialog, depending on your implementation
                // For example:
                // Intent editIntent = new Intent(getActivity(), EditPhotoActivity.class);
                // editIntent.putExtra("selectedPhotoPath", selectedPhotoPath);
                // startActivity(editIntent);
            }
        });

        return view;
    }
}



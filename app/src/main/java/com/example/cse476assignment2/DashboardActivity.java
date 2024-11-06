package com.example.cse476assignment2;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.cse476assignment2.databinding.ActivityDashboardBinding;
import java.util.ArrayList;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import static android.Manifest.permission.CAMERA;


public class DashboardActivity extends AppCompatActivity {

    // Binding object for the activity_dashboard layout
    private ActivityDashboardBinding binding;

    // List to hold tasks
    private ArrayList<Task> tasks = new ArrayList<>();

    //completed tasks
    private ArrayList<Task> completedTasks = new ArrayList<>();

    // Request code for starting AddTaskActivity
    private static final int REQUEST_CODE_ADD_TASK = 1;

    // Key for saving and restoring the task list state
    private static final String TASK_LIST_KEY = "task_list";

    //code for image capture
    private static final int REQUEST_IMAGE_CAPTURE = 22;

    //code for image request
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    //current task
    private Task currentTask;

    // current image view
    private ImageView currentImageView;

    // show completed tasks?
    private boolean showingCompletedTasks = false;

    private boolean camera = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore the task list if savedInstanceState contains it
        if (savedInstanceState != null && savedInstanceState.containsKey(TASK_LIST_KEY)) {
            tasks = (ArrayList<Task>) savedInstanceState.getSerializable(TASK_LIST_KEY);
        }

        // Inflate the layout using ViewBinding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the username from the Intent that started this activity
        String username = getIntent().getStringExtra("EXTRA_USERNAME");
        binding.usernameTextView.setText("Welcome, " + username);

        // Set an OnClickListener on the task button to start AddTaskActivity
        binding.taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
            }
        });

        // Set up the switch
        Switch taskSwitch = findViewById(R.id.history_switch);
        taskSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showingCompletedTasks = isChecked;
            displayTasks();
        });

        // Request camera permissions if not granted
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            if (!hasCamera()) {
                Toast.makeText(this, "No camera found on this device", Toast.LENGTH_SHORT).show();
                camera = false;
            }
        }

        // Display tasks in the task container after setting the layout
        displayTasks();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!hasCamera()) {
                    Toast.makeText(this, "No camera found on this device", Toast.LENGTH_SHORT).show();
                    camera = false;
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                camera = false;
            }
        }
    }

    private boolean hasCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        return numberOfCameras > 0;
    }

    // Method to display all tasks in the task container LinearLayout
    private void displayTasks() {
        // create container for task
        LinearLayout taskContainer = findViewById(R.id.taskContainer);
        taskContainer.removeAllViews();
        //determine which tasks to show
        ArrayList<Task> tasksToShow = showingCompletedTasks ? completedTasks : tasks;
        for (final Task task : tasksToShow) {
            LinearLayout taskLayout = createTaskLayout(task);
            taskContainer.addView(taskLayout);
        }
    }

    //function to create a task layout
    private LinearLayout createTaskLayout(final Task task) {
        //set linear layout for task
        LinearLayout taskLayout = new LinearLayout(this);
        taskLayout.setOrientation(LinearLayout.HORIZONTAL);

        //set text
        TextView taskTextView = new TextView(this);
        taskTextView.setText(task.getName() + " - " + task.getPoints() + " points");

        //set image view
        ImageView taskImageView = new ImageView(this);
        taskImageView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(90), dpToPx(90)));
        taskImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (task.getImageBitmap() != null) {
            taskImageView.setImageBitmap(task.getImageBitmap());
        }

        //add task and image
        taskLayout.addView(taskTextView);
        taskLayout.addView(taskImageView);

        //only for to do tasks add done button
        if (!showingCompletedTasks) {
            Button doneButton = new Button(this);
            doneButton.setText("Done");
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.getPhotoRequired() && camera) {
                        currentTask = task;
                        currentImageView = taskImageView;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } else if (task.getPhotoRequired() && !camera) {
                        Toast.makeText(DashboardActivity.this, "Please enable the camera to complete this task", Toast.LENGTH_SHORT).show();
                    } else {
                        completedTasks.add(task);
                        tasks.remove(task);
                        displayTasks();
                    }
                }
            });
            taskLayout.addView(doneButton);
        }

        return taskLayout;
    }
        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result comes from AddTaskActivity and if it was successful
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK && data != null) {
            // Retrieve the new task from the Intent
            Task newTask = (Task) data.getSerializableExtra("EXTRA_TASK");
            if (newTask != null) {
                // Add the new task to the task list and update the display
                tasks.add(newTask);
                displayTasks();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && camera) {
            // Check if the result comes from the camera and if it was successful
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImage(imageBitmap);
            if (currentTask != null && currentImageView != null) {
                currentImageView.setImageBitmap(imageBitmap);
                currentTask.setImageBitmap(imageBitmap);
                completedTasks.add(currentTask);
                tasks.remove(currentTask);
                currentTask = null;
                currentImageView = null;
            }
            displayTasks();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && !camera) {
            Toast.makeText(this, "Camera is necessary, please enable permissions", Toast.LENGTH_SHORT).show();

        }
    }

    // Your saveImage method implementation
    private void saveImage(Bitmap finalBitmap) {

    }

    // Utility method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the task list to the outState bundle
        outState.putSerializable(TASK_LIST_KEY, tasks);
    }
}
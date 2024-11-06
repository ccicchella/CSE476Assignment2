package com.example.cse476assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText;
    private EditText taskPointsEditText;
    private Button saveTaskButton;
    private Switch photoRequiredSwitch;

    @Override
    //Finds the value for the different texts and the button
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskNameEditText = findViewById(R.id.task_name);
        taskPointsEditText = findViewById(R.id.task_points);
        saveTaskButton = findViewById(R.id.save_task_button);
        photoRequiredSwitch = findViewById(R.id.photo_required);


        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // Lets you add tasks and store them
            public void onClick(View view) {
                String taskName = taskNameEditText.getText().toString();
                int taskPoints = Integer.parseInt(taskPointsEditText.getText().toString());

                boolean photoRequired = photoRequiredSwitch.isChecked();

                Task task = new Task(taskName, taskPoints, photoRequired);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("EXTRA_TASK", task);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
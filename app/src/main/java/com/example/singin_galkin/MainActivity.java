package com.example.singin_galkin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public int start_x = 0;
    private int startY;
    private int currentPage;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int endY = (int) event.getY();
                if (startY - endY > 50) { // Свайп вверх
                    goToNextPage();
                } else if (endY - startY > 50) { // Свайп вниз
                    goToPreviousPage();
                }
                break;
        }
        return true;
    }

    private void goToNextPage() {
        switch (currentPage) {
            case 0:
                setContentView(R.layout.regin);
                currentPage = 1;
                break;
            case 1:
                setContentView(R.layout.password);
                currentPage = 2;
                break;
        }
    }

    private void goToPreviousPage() {
        switch (currentPage) {
            case 1:
                setContentView(R.layout.activity_main);
                currentPage = 0;
                break;
            case 2:
                setContentView(R.layout.regin);
                currentPage = 1;
                break;
        }
    }

    public void onPassword(View view) {
        setContentView(R.layout.password);
    }

    public void onStart(View view) {
        EditText passwordEditText = findViewById(R.id.password);
        EditText repeatPasswordEditText = findViewById(R.id.reapeatPassword);

        String password = passwordEditText.getText().toString();
        String repeatPassword = repeatPasswordEditText.getText().toString();

        if (password.equals(repeatPassword)) {
            setContentView(R.layout.activity_main);
        } else {
            Toast toast = Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
package com.example.kurs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class settings extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioButton radioSystem, radioLight, radioDark;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String THEME_KEY = "theme_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        radioSystem = findViewById(R.id.radioSystem);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);

        loadThemeSetting();

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveThemeSetting(checkedId);
            }
        });
    }

    private void loadThemeSetting() {
        int themeMode = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        switch (themeMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                radioLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                radioDark.setChecked(true);
                break;
            default:
                radioSystem.setChecked(true);
                break;
        }
    }

    private void saveThemeSetting(int checkedId) {
        int themeMode;

        if (checkedId == R.id.radioLight) {
            themeMode = AppCompatDelegate.MODE_NIGHT_NO;
            Toast.makeText(this, "Светлая тема выбрана", Toast.LENGTH_SHORT).show();
        } else if (checkedId == R.id.radioDark) {
            themeMode = AppCompatDelegate.MODE_NIGHT_YES;
            Toast.makeText(this, "Темная тема выбрана", Toast.LENGTH_SHORT).show();
        } else {
            themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            Toast.makeText(this, "Системная тема выбрана", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(THEME_KEY, themeMode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(themeMode);
        recreate();
    }

    public void onBackClick(View view) {
        finish();
    }
}
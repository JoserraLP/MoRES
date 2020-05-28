package com.example.mqtt.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mqtt.DrawerActivity;
import com.example.mqtt.R;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner select_language = root.findViewById(R.id.select_language);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(requireContext(),
                        R.array.languages_array,
                        android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        select_language.setAdapter(adapter);

        select_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = parent.getItemAtPosition(position).toString();
                changeLanguage(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    private void changeLanguage(String language) {
        String languageCode = null;

        switch (language) {
            case "English":
            case "Inglés":
                languageCode = "en";
                break;
            case "Spanish":
            case "Español":
                languageCode = "es";
                break;
        }

        String currentLanguage = readValue("language");
        if (languageCode != null && !languageCode.equals(currentLanguage) && !currentLanguage.equals("")) {
            Locale locale = new Locale(languageCode); // where 'hi' is Language code, set this as per your Spinner Item selected
            Locale.setDefault(locale);
            Configuration config = requireContext().getResources().getConfiguration();
            config.setLocale(locale);
            //noinspection deprecation
            requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
            requireActivity().finish();
            requireActivity().startActivity(new Intent(getContext(), DrawerActivity.class));
        }

        SharedPreferences settings = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }

    private String readValue(@SuppressWarnings("SameParameterValue") String keyPref) {
        SharedPreferences preferences = requireContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

}

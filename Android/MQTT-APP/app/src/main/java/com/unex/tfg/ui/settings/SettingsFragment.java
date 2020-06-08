package com.unex.tfg.ui.settings;

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

import com.unex.tfg.DrawerActivity;
import com.unex.tfg.R;
import com.unex.tfg.utils.Constants;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    /**
     * onCreateView method
     * @param inflater Fragment inflater
     * @param container Fragment container
     * @param savedInstanceState Bundle where instance is saved
     * @return Fragment View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        // Spinner for language selector
        Spinner select_language = root.findViewById(R.id.select_language);

        // Language arrays adapter
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(requireContext(),
                        R.array.languages_array,
                        android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        select_language.setAdapter(adapter);

        // Spinner callback when item is selected
        select_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected language
                String language = parent.getItemAtPosition(position).toString();
                // Change the app language to the selected language
                changeLanguage(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    /**
     * Change the app language
     * @param language New app language
     */
    private void changeLanguage(String language) {
        String languageCode = null;

        // Switch selected language
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

        // Get app current language
        SharedPreferences settings = requireContext().getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
        String currentLanguage = settings.getString(Constants.PREFERENCES_LANGUAGE, "");

        if (languageCode != null && !languageCode.equals(currentLanguage) && !currentLanguage.equals("")) {
            // Create a Locale with the language code
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            // Configure the Locale
            Configuration config = requireContext().getResources().getConfiguration();
            config.setLocale(locale);

            //noinspection deprecation
            requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
            requireActivity().finish();
            requireActivity().startActivity(new Intent(getContext(), DrawerActivity.class));
        }

        // Save the language on the Shared Preferences
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(Constants.PREFERENCES_LANGUAGE, languageCode);
        editor.apply();
    }

}

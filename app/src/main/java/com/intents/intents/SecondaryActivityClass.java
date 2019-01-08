package com.intents.intents;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondaryActivityClass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary_activity);

        Bundle collectedData = getIntent().getExtras();
        String userData = collectedData.getString("textToShow");
        TextView text = findViewById(R.id.text_SecondaryActivity); //identificación de la vista creada en el layout de la segunda actividad
        text.setText(userData);

    }

}

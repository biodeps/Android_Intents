package com.intents.intents;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button takePictureButton; //S'HA DE DECLARAR EL BOTÓ AQUÍ A DALT PERQUÈ COM L'UTILITZAREM A UN OVERRIDE
    // (OnCreate) ves a saber si la funció OnRequestPermissionResult que l'utilitzarà el tindrà visible o no...

    Button pickairpick;

    ImageView image; //declaro la imatge que el meu programa utilitzara (idem que el takePictureButton)
    File file;

    EditText editText_Url;
    EditText editText_Phone;
    EditText editText_NewActivity;

    Spinner daySpinner;
    Spinner hourSpinner;
    Spinner minuteSpinner;
    Map<String, Integer> hashMapDays = new HashMap<String, Integer>();

    int spinnerHourPosition;
    int spinnerMinutePosition;
    int spinnerDayPosition;

    final int CAMERA = 100;
    final int BROWSEPIC = 101;
    final int CAMERAAIR = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imatgeResultat);
        image.setImageResource(R.drawable.provapaint); //omplo la imatge per defecte
        takePictureButton = findViewById(R.id.btn_takePic); //inicialitzo el botó que obre la camera
        pickairpick = findViewById(R.id.btn_pickAirPic);
        editText_Url = findViewById(R.id.txt_Url);
        editText_Phone = findViewById(R.id.txt_Phone);
        daySpinner = findViewById(R.id.day_spinner);
        hourSpinner = findViewById(R.id.hour_spinner);
        minuteSpinner = findViewById(R.id.minute_spinner);
        editText_NewActivity = findViewById(R.id.txt_NewActivity);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false); //si no tinc el permís, poso el botó a false perquè no permeti fer la foto
            pickairpick.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA);
            //Aquesta línia ha guardat els 2 permisos en una llista, però no cal fer-ho amb llistes
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // sobreescrivim la funció que diu què fer amb un request code. A l'IF anterior, li donavem un requestCode de 0
        //puc donar un codi diferent a cada request que calgui: Per exemple: 0 per a la càmera, 1 per a emmagatzemar, 2 per a GPS...
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA || requestCode == CAMERAAIR) { //si la petició de permís és la de la càmera
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //SI l'usuari ha acceptat TOTS els permisos (en aquest cas són 2)...
                takePictureButton.setEnabled(true);
                pickairpick.setEnabled(true);
                //permeto que s'utilitzi el botó de fer fotos
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //depenent dels requestCode, aquesta funció farà unes coses o altres
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == RESULT_OK && data != null) {

            //ERROR: E/Surface: getSlotFromBufferLocked: unknown buffer: 0xb30b9320
            //AQUÍ DEBE PROGRAMARSE EL SAVE & SHOW DE LA IMAGEN


        } else if (requestCode == BROWSEPIC && resultCode == RESULT_OK && data != null) {
            //busco una imatge a la galeria i la mostro
            try {
                InputStream myInputStream = getContentResolver().openInputStream(data.getData());
                BufferedInputStream myBuffer = new BufferedInputStream(myInputStream);
                Bitmap myBitmapImage = BitmapFactory.decodeStream(myBuffer);
                image.setImageBitmap(myBitmapImage);

            } catch (Exception e) {
                // Els toast sóns missatges que es mostren uns segons i desapareixen
                // http://www.sgoliver.net/blog/notificaciones-en-android-i-toast/
                Toast myToast = Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.toastText_error), Toast.LENGTH_LONG);
                // Per utilitzar un recurs de l'arxiu strings.xml es fa així ^
                myToast.show();
            }


        } else if (requestCode == CAMERAAIR && resultCode == RESULT_OK && data != null) {
            //sense guardar res, mostro la foto
            Bitmap photoToShow = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photoToShow);
        }
    }



    public void takePic(View vista) {
        Intent intentTakePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //declaro un INTENT de captura de imagen
        startActivityForResult(intentTakePic, CAMERA);

    }

    public void browseImage(View vista) {
        Intent intentBrowseImage = new Intent();
        intentBrowseImage.setType("image/*"); //declaro que l'intent és del tipus imatge
        intentBrowseImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentBrowseImage, this.getString(R.string.selectText_Pic)), BROWSEPIC);

    }

    public void pickAirPic(View vista) {
        // fa una foto i la mostra, sense emmagatzemar-la
        Intent intentPickAirPic = new Intent();
        intentPickAirPic.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentPickAirPic, CAMERAAIR);
    }

    public void goToWeb(View vista) {
        //valida la web i obre el navegador amb la pàgina escrita dins
        String url = editText_Url.getText().toString(); //getText és per obtenir el contingut, NO la direcció

        if (isMyUrlValid(url)) {
            if (!url.contains("http")){
                url = "http://" + url;
            }
            Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); //Action view és obrir una aplicació que pugui visualitzar coses
            startActivity(intentUrl); //No és ForResult, ja que no intento obtenir res: Vull simplement obrir una pàgina amb la web
        } else {
            //Toast al vuelo, sense declarar
            Toast.makeText(this, getApplicationContext().getString(R.string.toastText_Url), Toast.LENGTH_LONG).show();
        }

    }

    private boolean isMyUrlValid(String url) {
        //Expressions regulars Android check mail / url / lo que sea
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public void callToNumber(View vista) {
        // valida el número de telèfon i obre l'APP de telèfon per a confirmar la trucada
        String phoneNumber = editText_Phone.getText().toString();

        if (isMyNumberValid(phoneNumber)) {
            Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
            //ACTION_DIAL posa el número i permet a l'usuari decidir si fa la trucada o no, però
            //ACTION_CALL faria la trucada directament i per això REQUERIRIA PERMISSOS
            startActivity(intentPhone);
        } else {
            Toast.makeText(this, getApplicationContext().getString(R.string.toastText_Phone), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isMyNumberValid(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public void setAlarm(View vista) {
        spinnerHourPosition = hourSpinner.getSelectedItemPosition();
        String[] hourArray = getResources().getStringArray(R.array.hour_Array);

        spinnerMinutePosition = minuteSpinner.getSelectedItemPosition();
        String[] minuteArray = getResources().getStringArray(R.array.minute_Array);

        spinnerDayPosition = daySpinner.getSelectedItemPosition();
        String[] dayArray = getResources().getStringArray(R.array.days_Array);

        Intent intentAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        intentAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(hourArray[spinnerHourPosition]));
        intentAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(minuteArray[spinnerMinutePosition]));
        ArrayList<Integer> dayAlarmArray = new ArrayList<>();
        getMyMap(hashMapDays);

        dayAlarmArray.add(hashMapDays.get(dayArray[spinnerDayPosition]));
        intentAlarm.putExtra(AlarmClock.EXTRA_DAYS, dayAlarmArray);
        intentAlarm.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        startActivity(intentAlarm);
    }

    public void getMyMap(Map<String, Integer> hashMapDays) { //Map que transforma l'String del dia per l'enter que li pertoca
        hashMapDays.put("Sunday", Calendar.SUNDAY);
        hashMapDays.put("Monday", Calendar.MONDAY);
        hashMapDays.put("Tuesday", Calendar.TUESDAY);
        hashMapDays.put("Wednesday", Calendar.WEDNESDAY);
        hashMapDays.put("Thursday", Calendar.THURSDAY);
        hashMapDays.put("Friday", Calendar.FRIDAY);
        hashMapDays.put("Saturday", Calendar.SATURDAY);
    }

    public void openNewView(View vista) {
        String userText = editText_NewActivity.getText().toString(); //obtinc el text
        Intent openNewView = new Intent(this, SecondaryActivityClass.class);
        openNewView.putExtra("textToShow", userText); //afegeix el text com a "extra" dins l'intent
        startActivity(openNewView);
    }

    public void exitApp(View vista){
        finish();
    }

}

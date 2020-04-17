package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NoteBookActivity extends AppCompatActivity {

    public static ArrayList<String> noteBooks; //= new ArrayList<>();
    public static ArrayList<ArrayList<String>> noteBooksPages; //= new ArrayList<>();
    public static ArrayList<ArrayList<String>> noteBookPageTitlesList; //= new ArrayList<>();
    public static ArrayList<ArrayList<PageImageList>> noteBookPagesImages;
    public static ArrayList<Double[]> locations;
    static ArrayAdapter arrayAdapter;
    private ListView listView;
    private int STORAGE_PERMISSION_CODE = 1;
    public static String saveNoteBooksString = "notebook list";
    public static String saveNoteBooksPagesString = "notebook pages list";
    public static String saveNoteBooksPageTitleListString = "notebook page title string";
    public static String saveNoteBookPagePictures = "notebook pictures";
    private static final int Access_Photos = 1;

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
};
    private static final int LOCATION_REQUEST=1340;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);
        this.setTitle("NoteBooks");
        verifyPermission();

        Intent intent = getIntent();
        double newLatitude = intent.getDoubleExtra("latitude", 181.0);
        double newLongitude = intent.getDoubleExtra("longitude", 181.0);

        if (newLatitude != 181.0 && newLongitude != 181.0)
        {
            Double[] newLoc = {newLatitude, newLongitude};
            locations.add(newLoc);
        }

        requestPermissions(LOCATION_PERMS,LOCATION_REQUEST);
        listView = findViewById(R.id.listView);

        loadData();
//        noteBooks = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noteBooks);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteBookPages.class);
                intent.putExtra("noteBookID", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(NoteBookActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                        .setMessage("Do you want to delete this notebook?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteBooks.remove(position);
                        noteBookPageTitlesList.remove(position);
                        noteBooksPages.remove(position);
                        noteBookPagesImages.remove(position);
                        locations.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        saveData();
                    }
                }).setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
        GPSTracker tracker = new GPSTracker(getApplicationContext());
        ArrayList<String> tempNoteBooks = new ArrayList<>();
        ArrayList<ArrayList<String>> tempNotePageTitlesList = new ArrayList<>();
        ArrayList<ArrayList<String>> tempNotePages = new ArrayList<>();
        ArrayList<ArrayList<PageImageList>> tempNotePagesImages = new ArrayList<>();
        ArrayList<Double[]> tempLocs = new ArrayList<>();
        int counter = 0;
        for (String s : noteBooks)
        {
            if (!(locations.get(counter)[0] == 181.0))
            {
                if (tracker.location != null)
                {
                    Location mLocation = tracker.getLocation();

                    Double latitude = mLocation.getLatitude();
                    Double longitude = mLocation.getLongitude();

                    if (distance(latitude,longitude,locations.get(counter)[0],locations.get(counter)[1]) <= 1)
                    {
                        tempNoteBooks.add(noteBooks.get(counter));
                        tempNotePageTitlesList.add(noteBookPageTitlesList.get(counter));
                        tempNotePages.add(noteBooksPages.get(counter));
                        tempNotePagesImages.add(noteBookPagesImages.get(counter));
                        tempLocs.add(locations.get(counter));


                        noteBooks.remove(counter);
                        noteBookPageTitlesList.remove(counter);
                        noteBooksPages.remove(counter);
                        noteBookPagesImages.remove(counter);
                        locations.remove(counter);
                        arrayAdapter.notifyDataSetChanged();
                        saveData();

                        //Also part of the test.
                        noteBooks.add(0,tempNoteBooks.get(0));
                        noteBookPageTitlesList.add(0,tempNotePageTitlesList.get(0));
                        noteBooksPages.add(0,tempNotePages.get(0));
                        noteBookPagesImages.add(0,tempNotePagesImages.get(0));
                        locations.add(0,tempLocs.get(0));
                        arrayAdapter.notifyDataSetChanged();
                        saveData();
                        tempNoteBooks.clear();
                        tempNotePages.clear();
                        tempNotePagesImages.clear();
                        tempNotePageTitlesList.clear();
                        tempLocs.clear();

                    }
                }
            }
            counter++;

        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notebook_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Add_NoteBook:
                Intent intent = new Intent(getApplicationContext(), NoteBookPages.class);
                startActivity(intent);
                Double[] temp = {181.0,181.0};
                locations.add(temp);
                saveData();
                return true;
            case R.id.Delete_Notebooks:
                new AlertDialog.Builder(NoteBookActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                        .setMessage("Do you want to delete all your notebooks?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearData();
                        loadData();
                        arrayAdapter.clear();
                        arrayAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null)
                        .show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteBooks);
        editor.putString(saveNoteBooksString,json);

        editor.apply();

    }
    private void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(saveNoteBooksString,null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        noteBooks = gson.fromJson(json,type);
        if(noteBooks ==null){
            noteBooks=new ArrayList<>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(saveNoteBooksPagesString, null);
        type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
        noteBooksPages = gson.fromJson(json, type);
        if(noteBooksPages == null)
        {
            noteBooksPages = new ArrayList<>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(saveNoteBooksPageTitleListString, null);
        type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
        noteBookPageTitlesList = gson.fromJson(json, type);
        if(noteBookPageTitlesList == null)
        {
            noteBookPageTitlesList = new ArrayList<>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(saveNoteBookPagePictures, null);
        type = new TypeToken<ArrayList<ArrayList<PageImageList>>>(){}.getType();
        noteBookPagesImages = gson.fromJson(json, type);
        if(noteBookPagesImages == null)
        {
            noteBookPagesImages = new ArrayList<>();
        }
    }

    private void clearData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void verifyPermission()
    {


        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
        //Log.d("Permissions", "Requesting permissions: " + permission);

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[0]) == PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(this, permission, Access_Photos);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermission();
    }

}

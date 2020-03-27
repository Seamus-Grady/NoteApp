package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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

    public static ArrayList<String> noteBooks = new ArrayList<>();
    public static ArrayList<ArrayList<Editable>> noteBooksPages = new ArrayList<>();
    public static ArrayList<ArrayList<String>> noteBookPageTitlesList = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    private ListView listView;
    private int STORAGE_PERMISSION_CODE = 1;
    public static String saveNoteBooksString = "notebook list";
    public static String saveNoteBooksPagesString = "notebook pages list";
    public static String saveNoteBooksPageTitleListString = "notebook page title string";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);
        this.setTitle("NoteBooks");
        //clearData();
        listView = findViewById(R.id.listView);

        //loadData();
        //noteBooks = new ArrayList<>();

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
                        arrayAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    public void saveData()
//    {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(noteBooks);
//        editor.putString(saveNoteBooksString,json);
//
//        editor.apply();
//
//    }
//    private void loadData()
//    {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString(saveNoteBooksString,null);
//        Type type = new TypeToken<ArrayList<String>>() {}.getType();
//        noteBooks = gson.fromJson(json,type);
//        if(noteBooks ==null){
//            noteBooks=new ArrayList<>();
//        }
//
//        json = sharedPreferences.getString(saveNoteBooksPagesString, null);
//        type = new TypeToken<ArrayList<String>>(){}.getType();
//        JsonArray test = gson.fromJson(json, JsonArray.class);
////        noteBooksPages = gson.fromJson(json, type);
//        if(test == null)
//        {
//            noteBooksPages = new ArrayList<>();
//        }
//        else
//        {
//            ArrayList<Editable> test1 = new ArrayList<>();
//            for(int i = 0; i < test.size(); i++)
//            {
//                Editable test2 = (Editable) test.get(i);
//            }
//        }
//
//        json = sharedPreferences.getString(saveNoteBooksPageTitleListString, null);
//        type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
//        noteBookPageTitlesList = gson.fromJson(json, type);
//        if(noteBookPageTitlesList == null)
//        {
//            noteBookPageTitlesList = new ArrayList<>();
//        }
//    }

    private void clearData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}

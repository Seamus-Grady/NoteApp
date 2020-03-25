package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class NoteBookPages extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    public static ArrayList<Editable> noteBookPages = new ArrayList<Editable>();
    public static ArrayList<String> noteBookPageTitles = new ArrayList<String>();
    public static ArrayAdapter arrayAdapter;
    private int noteBookID;
    private int titleSaved = 0;
//    private boolean backButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_pages);
        listView = findViewById(R.id.noteBookPages);
        editText = findViewById(R.id.noteBookName);
        Intent intent = getIntent();
        noteBookID = intent.getIntExtra("noteBookID", -1);
        if(noteBookID != -1)
        {
            this.setTitle(NoteBookActivity.noteBooks.get(noteBookID));
            editText.setText(NoteBookActivity.noteBooks.get(noteBookID));
            titleSaved = 1;
        }
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noteBookPageTitles);
        listView.setAdapter(arrayAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleSaved = -1;
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_UP && !v.hasFocus())
                {
                    v.performClick();
                }
                return false;
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(editText.getText().toString().equals("Title"))
                {
                    editText.setText("");
                    editText.setSelection(editText.getText().length());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (titleSaved)
        {
            case -1:
                new AlertDialog.Builder(NoteBookPages.this)
                        .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                        .setMessage("Do you want to save your notebook's title?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(noteBookID == -1)
                        {
                            NoteBookActivity.noteBooks.add(editText.getText().toString());
                            NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                            NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                        }
                        NoteBookPages.super.onBackPressed();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteBookPages.super.onBackPressed();
                    }
                })
                        .show();
                break;
            case 0:
                NoteBookActivity.noteBooks.add("");
                NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                NoteBookPages.super.onBackPressed();
                break;
            case 1:
                NoteBookPages.super.onBackPressed();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_a_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Save:
                this.setTitle(editText.getText().toString());
                if(noteBookID == -1)
                {
                    NoteBookActivity.noteBooks.add(editText.getText().toString());
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                hideAKeyboard(this);
                titleSaved = 1;
                return true;
            case R.id.Add_Page:
                if(noteBookID == -1)
                {
                    NoteBookActivity.noteBooks.add(editText.getText().toString());
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                titleSaved = 1;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("New Page Name");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                        intent.putExtra("pageName", input.getText().toString());
                        startActivity(intent);
                        NoteBookPages.this.setTitle(editText.getText().toString());
                        findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                        hideAKeyboard(NoteBookPages.this);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                        intent.putExtra("pageName", "");
                        startActivity(intent);
                        NoteBookPages.this.setTitle(editText.getText().toString());
                        findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                        hideAKeyboard(NoteBookPages.this);
                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private static void hideAKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

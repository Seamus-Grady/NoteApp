package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class NoteBookPages extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    public static ArrayList<String> noteBookPages;
    public static ArrayList<String> noteBookPageTitles;
    public static ArrayList<PageImageList> imagesForPage;
    public static ArrayAdapter arrayAdapter;
    private int noteBookID;
    private boolean defaultState;
    private String defaultTitle;
    private int STORAGE_PERMISSION_CODE = 1;
    private double latitude;
    private double longitude;
    public static ArrayList<double[]> locationTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_pages);
        listView = findViewById(R.id.noteBookPages);
        editText = findViewById(R.id.noteBookName);
        //LoadData
        if(savedInstanceState != null)
        {
            noteBookID = savedInstanceState.getInt("noteBookID");
            latitude = savedInstanceState.getDouble("lattitude");
            longitude = savedInstanceState.getDouble("longitude");
        }
        else {
            Intent intent = getIntent();
            noteBookID = intent.getIntExtra("noteBookID", -1);
            latitude = intent.getDoubleExtra("latitude", 181.0);
            longitude = intent.getDoubleExtra("longitude", 181.0);
        }
        if(noteBookID != -1)
        {
            //LoadData();//////figuring out what to load here/////////////
            this.setTitle(NoteBookActivity.noteBooks.get(noteBookID));
            editText.setText(NoteBookActivity.noteBooks.get(noteBookID));
            defaultTitle = NoteBookActivity.noteBooks.get(noteBookID);
            noteBookPages = NoteBookActivity.noteBooksPages.get(noteBookID);
            noteBookPageTitles = NoteBookActivity.noteBookPageTitlesList.get(noteBookID);
            imagesForPage = NoteBookActivity.noteBookPagesImages.get(noteBookID);
            defaultState = false;
        }
        else
        {
            this.setTitle("NoteBook");
            defaultTitle = "Title";
            noteBookPageTitles = new ArrayList<>();
            noteBookPages = new ArrayList<>();
            imagesForPage = new ArrayList<>();
            defaultState = true;

        }

        if (latitude != 181.0 && longitude != 181.0)
        {
            double[] newLoc = new double[2];
            newLoc[0] = latitude;
            newLoc[1] = longitude;
            locationTags.add(newLoc);
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noteBookPageTitles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                LinearLayout etLayout = new LinearLayout(NoteBookPages.this);
                etLayout.setOrientation(LinearLayout.HORIZONTAL);

                final AlertDialog.Builder settings = new AlertDialog.Builder(NoteBookPages.this);

                settings.setTitle("Options");

                Button changeTitle = new Button(NoteBookPages.this);
                changeTitle.setText("Change Title");

                changeTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(NoteBookPages.this);
                        builder.setTitle("New Page Name");

                        final EditText input = new EditText(NoteBookPages.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteBookPages.noteBookPageTitles.set(position, input.getText().toString());
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();


                    }
                });

                etLayout.addView(changeTitle);

                Button deletePage = new Button(NoteBookPages.this);
                deletePage.setText("Delete Page");



                etLayout.addView(deletePage);

                settings.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                settings.setView(etLayout);

                final AlertDialog settingsDialog = settings.create();

                settingsDialog.show();

                deletePage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(NoteBookPages.this)
                                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                                .setMessage("Do you want to delete this page?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteBookPages.noteBookPageTitles.remove(position);
                                imagesForPage.remove(position);
                                noteBookPages.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                saveData();
                                settingsDialog.hide();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingsDialog.hide();
                            }
                        })
                                .show();

                    }
                });

                changeTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(NoteBookPages.this);
                        builder.setTitle("New Page Name");

                        final EditText input = new EditText(NoteBookPages.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteBookPages.noteBookPageTitles.set(position, input.getText().toString());
                                arrayAdapter.notifyDataSetChanged();
                                settingsDialog.hide();
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingsDialog.hide();
                            }
                        });
                        builder.show();



                    }
                });
                return  true;

            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //titleSaved = -1;
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                intent.putExtra("pageID", position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("noteBookID", noteBookID);
        outState.putDouble("lattitude", latitude);
        outState.putDouble("longitude", longitude);
    }

    @Override
    public void onBackPressed() {
        if(!editText.getText().toString().equals(defaultTitle))
        {
            new AlertDialog.Builder(NoteBookPages.this)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                    .setMessage("Do you want to save your notebook's title?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(NoteBookActivity.noteBooksPages.size()-1 != noteBookID || noteBookID == -1)
                    {
                        NoteBookActivity.noteBooks.add(editText.getText().toString());
                        NoteBookActivity.noteBooksPages.add(noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                        NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    }
                    NoteBookPages.super.onBackPressed();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(noteBookID == -1)
                    {
                        NoteBookActivity.noteBooks.add("Untitled Notebook");
                        NoteBookActivity.noteBooksPages.add(noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    }
                    NoteBookPages.super.onBackPressed();
                }
            })
                    .show();
            saveData();
        }
        else
        {
                if(defaultState)
                {
                    if (NoteBookActivity.noteBooksPages.size()-1 != noteBookID || noteBookID == -1) {
                        NoteBookActivity.noteBooks.add("Untitled Notebook");
                        NoteBookActivity.noteBooksPages.add(noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    } else {
                        NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    }
                    NoteBookPages.super.onBackPressed();
                    //saveData();
                }
                else {
                        NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                        NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                        NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                        NoteBookActivity.arrayAdapter.notifyDataSetChanged();

                    //saveData();
                    NoteBookPages.super.onBackPressed();
                }
                saveData();
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
        hideAKeyboard(this);
        switch (item.getItemId())
        {
            case R.id.Save:
                this.setTitle(editText.getText().toString());
                if(NoteBookActivity.noteBooksPages.size()-1 != noteBookID || noteBookID == -1)
                {
                    NoteBookActivity.noteBooks.add(editText.getText().toString());
                    NoteBookActivity.noteBooksPages.add(noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    noteBookID = NoteBookActivity.noteBooksPages.size()-1;
                }
                else
                {
                    NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                    NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                hideAKeyboard(this);
//                titleSaved = 1;
                defaultState = false;
                defaultTitle = editText.getText().toString();
                saveData();
                return true;
            case R.id.Add_Page:
                if(noteBookID == -1)
                {
                    if(editText.getText().toString().equals("Title"))
                    {
                        NoteBookActivity.noteBooks.add("Untitled Notebook");
                        defaultTitle = "Untitled Notebook";
                    }
                    else
                    {
                        NoteBookActivity.noteBooks.add(editText.getText().toString());
                        defaultTitle = editText.getText().toString();
                    }
                    NoteBookActivity.noteBooksPages.add(noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    noteBookID = NoteBookActivity.noteBooksPages.size()-1;
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                    NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    defaultTitle = editText.getText().toString();
                }
                defaultState = false;
//                titleSaved = 1;
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
                        findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                        hideAKeyboard(NoteBookPages.this);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                        intent.putExtra("pageName", "Untitled Page");
                        startActivity(intent);
                        findViewById(R.id.noteBookPageLayoutActivity).requestFocus();
                        hideAKeyboard(NoteBookPages.this);
                    }
                });
                builder.show();
                this.setTitle(NoteBookActivity.noteBooks.get(NoteBookActivity.noteBooks.size()-1));
                editText.setText(NoteBookActivity.noteBooks.get(NoteBookActivity.noteBooks.size()-1));
                editText.setSelection(editText.getText().length());
                saveData();
                return true;
            case R.id.Add_Location:
                this.setTitle(editText.getText().toString());
                if(noteBookID == -1)
                {
                    NoteBookActivity.noteBooks.add(editText.getText().toString());
                    NoteBookActivity.noteBooksPages.add(noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.add(noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.add(imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                    noteBookID = NoteBookActivity.noteBooksPages.size()-1;
                }
                else
                {
                    NoteBookActivity.noteBooks.set(noteBookID ,editText.getText().toString());
                    NoteBookActivity.noteBooksPages.set(noteBookID, noteBookPages);
                    NoteBookActivity.noteBookPageTitlesList.set(noteBookID, noteBookPageTitles);
                    NoteBookActivity.noteBookPagesImages.set(noteBookID, imagesForPage);
                    NoteBookActivity.arrayAdapter.notifyDataSetChanged();
                }
                defaultState = false;
                defaultTitle = editText.getText().toString();
                saveData();
                if (NoteBookActivity.locations.get(noteBookID)[0] == 181.0)
                {
                    new AlertDialog.Builder(NoteBookPages.this)
                            .setTitle("No existing tag found!")
                            .setMessage("Do you want to tag a new location?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                    intent.putExtra("notebookID", noteBookID);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", null)
                            .show();
                }
                else
                {
                    new AlertDialog.Builder(NoteBookPages.this)
                            .setTitle("Existing tag found!")
                            .setMessage("Current tag:\nLatitude: " + NoteBookActivity.locations.get(noteBookID)[0] +
                                    "\nLongitude: " + NoteBookActivity.locations.get(noteBookID)[1] +
                                    "\nDo you want to replace this tag?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                    intent.putExtra("notebookID", noteBookID);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", null)
                            .show();
                }

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

    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(NoteBookActivity.noteBooks);
        editor.putString(NoteBookActivity.saveNoteBooksString,json);


        gson = new Gson();
        json = gson.toJson(NoteBookActivity.noteBooksPages);
        editor.putString(NoteBookActivity.saveNoteBooksPagesString, json);

        gson = new Gson();
        json = gson.toJson(NoteBookActivity.noteBookPageTitlesList);
        editor.putString(NoteBookActivity.saveNoteBooksPageTitleListString, json);

        gson = new Gson();
        json = gson.toJson(NoteBookActivity.noteBookPagesImages);
        editor.putString(NoteBookActivity.saveNoteBookPagePictures, json);

        gson = new Gson();
        json = gson.toJson(NoteBookActivity.locations);
        editor.putString(NoteBookActivity.saveLocations, json);

        editor.apply();
    }

}

package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.net.Uri;
import android.database.Cursor;
import android.widget.LinearLayout;

public class PageActivity extends AppCompatActivity {

    private static int resultLoadImage = 1;
    private EditText editText;
    private String pageTitle = "Example Note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(pageTitle);
        setContentView(R.layout.activity_page);
        editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.page_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Insert_Image:
                Intent i = new Intent(
                       Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, resultLoadImage);
                return  true;
            case R.id.Insert_Link:
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Insert Link");

                final EditText linkText = new EditText(this);
                linkText.setHint("Link Text:");
                linearLayout.addView(linkText);

                final EditText linkURL = new EditText(this);
                linkURL.setHint("Link URL:");
                linearLayout.addView(linkURL);

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: possibly add null checks
                        String newLinkText = "" + linkText.getText();
                        String newLinkURL = "" + linkURL.getText();

                        editText.append("\r\n" + newLinkText + ": ");

                        editText.setMovementMethod(LinkMovementMethod.getInstance());
                        SpannableString ss = new SpannableString(newLinkURL);
                        Linkify.addLinks(ss, Linkify.WEB_URLS);
                        CharSequence text = TextUtils.concat(ss, "\u200B");
                        editText.append(text);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setView(linearLayout);

                AlertDialog dialog = builder.create();

                dialog.show();
             default:
                 return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == resultLoadImage && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            String [] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageSpan imageSpan = new ImageSpan(this, selectedImage);
            if(editText.getText().length() == 0|| editText.getText().toString().endsWith("\n"))
            {
                editText.append(" ");
            }
            else
            {
                editText.append("\n" + " ");
            }
            SpannableString spannableString = new SpannableString(editText.getText());
            spannableString.setSpan(imageSpan, editText.getText().length()-1 , editText.getText().length(), 0);
            editText.setText(spannableString);
            editText.append("\n");
            editText.setSelection(editText.getText().length());
        }
    }
}

package com.example.noteapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.net.Uri;
import android.database.Cursor;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//import com.google.gson.Gson;

public class PageActivity extends AppCompatActivity {

    private static int resultLoadImage = 1;
    private EditText editText;
    private String pageTitle;
    private int pageID;
    private PageImageList pageImageList;
    private ImageSpan[] checkSpans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(pageTitle);
        setContentView(R.layout.activity_page);
        editText = findViewById(R.id.editText);
        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageName");
        pageID = intent.getIntExtra("pageID", -1);
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        if(pageID == -1)
        {
            this.setTitle(pageTitle);
            pageImageList = new PageImageList();
            checkSpans = new ImageSpan[0];
        }
        else
        {
            this.setTitle(NoteBookPages.noteBookPageTitles.get(pageID));
            //editText.setText(new SpannableString(Html.fromHtml(NoteBookPages.noteBookPages.get(pageID))));
            pageImageList = NoteBookPages.imagesForPage.get(pageID);
            editText.setText(Html.fromHtml(NoteBookPages.noteBookPages.get(pageID)));
            for(int i = 0; i <NoteBookPages.imagesForPage.get(pageID).pageImageList.size(); i++)
            {
                ImageSpan imageSpan = null;
                try {
//                imageSpan = new ImageSpan(this, decodeUri(this, selectedImage, 100));
//                    editText.setText(new char[]{' '}, pageImageList.getStartIndex(i), 1);
                        imageSpan = new ImageSpan(this, getCorrectlyOrientedImage(this, Uri.parse(pageImageList.getUri(i)), 700));
                        SpannableString spannableString = new SpannableString(editText.getText());
                        spannableString.setSpan(imageSpan, pageImageList.getStartIndex(i) , pageImageList.getStopIndex(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editText.setText(spannableString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            checkSpans = editText.getText().getSpans(0, editText.length(), ImageSpan.class);
        }

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
    public void onBackPressed()
    {
        ImageSpan [] toRemoveSpan = editText.getText().getSpans(0, editText.getText().length(), ImageSpan.class);
        checkSpans(toRemoveSpan);
        for(int i = 0; i < toRemoveSpan.length; i++)
        {
            int start = editText.getText().getSpanStart(toRemoveSpan[i]);
            int end = editText.getText().getSpanEnd(toRemoveSpan[i]);
            editText.getText().replace(start, end, " ");
            editText.getText().removeSpan(toRemoveSpan[i]);
            pageImageList.pageImageList.get(i).startIndex = start;
            pageImageList.pageImageList.get(i).stopIndex = end;
            //editText.getText().insert(pageImageList.getStartIndex(i), " ");
        }
        if(pageID == -1)
        {
            NoteBookPages.noteBookPages.add(Html.toHtml(editText.getText()));
            NoteBookPages.noteBookPageTitles.add(pageTitle);
            NoteBookPages.imagesForPage.add(pageImageList);
            NoteBookPages.arrayAdapter.notifyDataSetChanged();
            super.onBackPressed();
        }
        else {
            NoteBookPages.noteBookPages.set(pageID, Html.toHtml(editText.getText()));
            NoteBookPages.imagesForPage.set(pageID, pageImageList);
            super.onBackPressed();
        }
    }

    private void checkSpans(ImageSpan[] arrayToCheck)
    {
        int indexToRemove = 0;
        int lastIndex = 0;
        int finalIndex;
        ArrayList<PageImageList.Image> imagesToRemove = new ArrayList<>();
        for(finalIndex = 0; finalIndex < arrayToCheck.length; finalIndex++)
        {
            if(!checkSpans[finalIndex].equals(arrayToCheck[finalIndex]))
            {
                if(indexToRemove == 0)
                {
                    indexToRemove = finalIndex;
                    lastIndex = indexToRemove+1;
                }
                lastIndex++;
            }
            else if(checkSpans[finalIndex].equals(arrayToCheck[finalIndex]) && indexToRemove != 0)
            {
                for(int j = indexToRemove; j < lastIndex; j++)
                {
                    imagesToRemove.add(pageImageList.pageImageList.get(j));
                }
                indexToRemove = 0;
            }
        }
        for(int i = finalIndex; i < checkSpans.length; i++)
        {
            imagesToRemove.add(pageImageList.pageImageList.get(i));
        }
        pageImageList.pageImageList.removeAll(imagesToRemove);
    }

    private ArrayList<String> createFinalString() {
        ArrayList<String> returnList = new ArrayList<>();
        if(pageImageList.pageImageList.size() == 0)
        {
            returnList.add(Html.toHtml(editText.getText()));
        }
        else {
            int startIndex = 0;
            for (int i = 0; i < pageImageList.pageImageList.size(); i++) {
                String s = editText.getText().subSequence(0, pageImageList.getStartIndex(i)).toString();
                returnList.add(Html.toHtml(new SpannableString(editText.getText().subSequence(startIndex, pageImageList.getStartIndex(i)))));
                startIndex = pageImageList.getStopIndex(i)+1;
            }
            returnList.add(Html.toHtml(new SpannableString(editText.getText().subSequence(startIndex, editText.getText().length()))));
        }

        return returnList;

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
                        // TODO: REMOVE THIS AFTER TESTING
                        Test();
                        dialog.cancel();
                    }
                });

                builder.setView(linearLayout);

                AlertDialog dialog = builder.create();

                dialog.show();

                return true;

            case R.id.Edit_Text:
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                if (start > end)
                {
                    int temp = end;
                    end = start;
                    start = temp;
                }

                final String selectedText = editText.getText().toString().substring(start, end);

                final SpannableString spannedText = new SpannableString(editText.getText());
                final CharSequence firstPart = spannedText.subSequence(0, start);
                final CharSequence secondPart = spannedText.subSequence(end, editText.length());
                final SpannableString selected = new SpannableString(spannedText.subSequence(start, end).toString());

                LinearLayout etLayout = new LinearLayout(this);
                etLayout.setOrientation(LinearLayout.VERTICAL);

                final AlertDialog.Builder etBuilder = new AlertDialog.Builder(this);
                etBuilder.setTitle("Edit Text");

                final CheckBox boldCheck = new CheckBox(this);
                boldCheck.setText("Bold");
                etLayout.addView(boldCheck);

                final CheckBox italicsCheck = new CheckBox(this);
                italicsCheck.setText("Italics");
                etLayout.addView(italicsCheck);

                final CheckBox underlineCheck = new CheckBox(this);
                underlineCheck.setText("Underline");
                etLayout.addView(underlineCheck);

                String[] sizes = {"Tiny", "Small", "Normal", "Large", "Massive"};
                final Spinner spinner = new Spinner(this);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sizes);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
                spinner.setSelection(2);
                etLayout.addView(spinner);

                etBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!selectedText.isEmpty())
                        {
                            if (spinner.getSelectedItem().equals("Tiny"))
                            {
                                selected.setSpan(new AbsoluteSizeSpan(10, true), 0, selected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else if (spinner.getSelectedItem().equals("Small"))
                            {
                                selected.setSpan(new AbsoluteSizeSpan(12, true), 0, selected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else if (spinner.getSelectedItem().equals("Normal"))
                            {
                                selected.setSpan(new AbsoluteSizeSpan(18, true), 0, selected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else if (spinner.getSelectedItem().equals("Large"))
                            {
                                selected.setSpan(new AbsoluteSizeSpan(24, true), 0, selected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else if (spinner.getSelectedItem().equals("Massive"))
                            {
                                selected.setSpan(new AbsoluteSizeSpan(36, true), 0, selected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                            if (boldCheck.isChecked())
                            {
                                selected.setSpan(new StyleSpan(Typeface.BOLD), 0, selected.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (italicsCheck.isChecked())
                            {
                                selected.setSpan(new StyleSpan(Typeface.ITALIC), 0, selected.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (underlineCheck.isChecked())
                            {
                                selected.setSpan(new UnderlineSpan(), 0, selected.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            SpannableStringBuilder goodText = new SpannableStringBuilder(firstPart);
                            goodText.append(selected);
                            goodText.append(secondPart);
                            editText.setText(goodText);
                        }
                        dialog.dismiss();
                    }
                });

                etBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                etBuilder.setView(etLayout);

                AlertDialog etDialog = etBuilder.create();

                etDialog.show();

                return true;

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


            ImageSpan imageSpan = null;
            try {
//                imageSpan = new ImageSpan(this, decodeUri(this, selectedImage, 100));
                imageSpan = new ImageSpan(this, getCorrectlyOrientedImage(this, selectedImage, 700));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Drawable d = imageSpan.getDrawable();
//            d.setBounds(0,0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            if(editText.getText().length() == 0|| editText.getText().toString().endsWith("\n"))
            {
                editText.append(" ");
            }
            else
            {
                editText.append("\r\n" + " ");
            }
            SpannableString spannableString = new SpannableString(editText.getText());
            spannableString.setSpan(imageSpan, editText.getText().length()-1 , editText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(spannableString);
            pageImageList.addAnImage(editText.getText().length()-1, editText.getText().length(), selectedImage.toString());
//            if(pageID == -1)
//            {
//                pageImageList.addAnImage(editText.getText().length()-1, editText.getText().length(), selectedImage);
//            }
//            else
//            {
//                NoteBookPages.imagesForPage.get(pageID).addAnImage(editText.getText().length()-1, editText.getText().length(), selectedImage);
//            }
            editText.append("\r\n");
            editText.setSelection(editText.getText().length());
            checkSpans = editText.getText().getSpans(0, editText.length(), ImageSpan.class);
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
    private static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }
//    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
//        ExifInterface ei = new ExifInterface(image_absolute_path);
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return rotate(bitmap, 90);
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return rotate(bitmap, 180);
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return rotate(bitmap, 270);
//
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                return flip(bitmap, true, false);
//
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                return flip(bitmap, false, true);
//
//            default:
//                return bitmap;
//        }
//    }
//
//    public static Bitmap rotate(Bitmap bitmap, float degrees) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degrees);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }
//
//    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
//        Matrix matrix = new Matrix();
//        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

    public static int getOrientation(Context context, Uri photoUri) {

        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return 90;  //Assuming it was taken portrait
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Rotates and shrinks as needed
     */
    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri, int maxWidth)
            throws IOException {

        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();


        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            Log.d("ImageUtil", "Will be rotated");
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        Log.d("ImageUtil", String.format("rotatedWidth=%s, rotatedHeight=%s, maxWidth=%s",
                rotatedWidth, rotatedHeight, maxWidth));
        if (rotatedWidth > maxWidth || rotatedHeight > maxWidth) {
            float widthRatio = ((float) rotatedWidth) / ((float) maxWidth);
            float heightRatio = ((float) rotatedHeight) / ((float) maxWidth);
            float maxRatio = Math.max(widthRatio, heightRatio);
            Log.d("ImageUtil", String.format("Shrinking. maxRatio=%s",
                    maxRatio));

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            Log.d("ImageUtil", String.format("No need for Shrinking. maxRatio=%s",
                    1));

            srcBitmap = BitmapFactory.decodeStream(is);
            Log.d("ImageUtil", String.format("Decoded bitmap successful"));
        }
        ((InputStream) is).close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }


    private void Test()
    {
        String s = Html.toHtml(editText.getText());

        SpannableString ss = new SpannableString(Html.fromHtml(s));

        editText.setText(ss);
    }

}

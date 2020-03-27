package com.example.noteapptest;

import android.net.Uri;

import java.util.ArrayList;

public class PageImageList {
    public ArrayList<Image> pageImageList;

    public PageImageList()
    {
        pageImageList = new ArrayList<Image>();
    }


    public void addAnImage(int start, int stop, Uri uri)
    {
        Image currentImageToAdd = new Image(start, stop, uri);
        pageImageList.add(currentImageToAdd);
    }

    public int getStartIndex(int position)
    {
        return pageImageList.get(position).startIndex;
    }

    public int getStopIndex(int position)
    {
        return pageImageList.get(position).stopIndex;
    }

    public Uri getUri(int position)
    {
        return pageImageList.get(position).uri;
    }

    private class Image{
        public int startIndex;
        public int stopIndex;
        public Uri uri;

        public Image(int start, int stop, Uri currentUri)
        {
            startIndex = start;
            stopIndex = stop;
            uri = currentUri;
        }
    }
}

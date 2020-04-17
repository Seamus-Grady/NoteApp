package com.example.noteapptest;

import android.net.Uri;

import java.util.ArrayList;

public class PageImageList {
    public ArrayList<Image> pageImageList;

    public PageImageList()
    {
        pageImageList = new ArrayList<Image>();
    }


    public void addAnImage(int start, int stop, String uri)
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

    public String getUri(int position)
    {
        return pageImageList.get(position).uriPath;
    }

    public class Image{
        public int startIndex;
        public int stopIndex;
        public String uriPath;

        public Image(int start, int stop, String currentUri)
        {
            startIndex = start;
            stopIndex = stop;
            uriPath = currentUri;
        }
    }
}

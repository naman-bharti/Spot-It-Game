package ca.cmpt276.userstories1.UI;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.userstories1.R;
import ca.cmpt276.userstories1.model.FlickrFetchr;
import ca.cmpt276.userstories1.model.GalleryItem;
import ca.cmpt276.userstories1.model.QueryPreferences;
import ca.cmpt276.userstories1.model.ThumbnailDownloader;

/**
 * Shows the Flickr photo gallery in a scrollable matter
 * Copied and modified code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
 */


// CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip

public class PhotoGalleryFragment extends Fragment {
    private static final String FLICKR_IMAGES_PREF = "FLICKR_IMAGES_PREF";
    private static final String TAG = "PhotoGalleryFragment";

    private List<String> imgs;

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

        // Citation: https://stackoverflow.com/questions/22984696/storing-array-list-object-in-sharedpreferences
        // Gets saved array of URLs
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        if (!(json.equals(""))) {
            imgs = gson.fromJson(json, type);
        } else {
            imgs = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(getActivity(), s);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    public static class customTag {
        boolean isGreyed;
        String url;
    }

    // Citation: https://stackoverflow.com/questions/25454316/how-do-i-partially-gray-out-an-image-when-pressed
    private boolean greyOut(ImageView view) {
        boolean isGreyed = ((customTag) view.getTag()).isGreyed;
        if (!isGreyed) {
            view.setColorFilter(Color.argb(150, 200, 200, 200));
            customTag newTag = ((customTag) view.getTag());
            newTag.isGreyed = true;
            view.setTag(newTag);
            return true;
        } else {
            view.setColorFilter(null);
            customTag newTag = ((customTag) view.getTag());
            newTag.isGreyed = false;
            view.setTag(newTag);
            return false;
        }
    }

    private void getImage(ImageView view) {
        if (view.getTag() == null) {
            // Image is not loaded yet?
            return;
        }

        try {
            String url = (String) ((customTag) view.getTag()).url;
            if (greyOut(view)) {
                Toast.makeText(getActivity(), "Added image!", Toast.LENGTH_SHORT).show();
                if (!imgs.contains(url)) {
                    imgs.add(url);
                }
            } else {
                Toast.makeText(getActivity(), "Removed image!", Toast.LENGTH_SHORT).show();
                imgs.remove(url);
            }
            saveThisList(imgs);
        } catch (ClassCastException e) {
            // Better to be not responsive, then to crash
            return;
        }
    }

    // Citation: https://stackoverflow.com/questions/22984696/storing-array-list-object-in-sharedpreferences
    // Saves the imgs to shared preferences
    private void saveThisList(List<String> imgs) {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(imgs);
        editor.putString(FLICKR_IMAGES_PREF, json);
        editor.apply();
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = itemView.findViewById(R.id.item_image_view);
        }

        public void setTag(customTag tag) {
            mItemImageView.setTag(tag);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.flag01_hk);
            photoHolder.bindDrawable(placeholder);
            photoHolder.mItemImageView.setColorFilter(null);
            customTag tag = new customTag();
            tag.isGreyed = false;
            tag.url = galleryItem.getUrl();
            photoHolder.setTag(tag);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());

            photoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getImage((ImageView) v);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }

    }

}

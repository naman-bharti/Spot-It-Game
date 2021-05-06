package ca.cmpt276.userstories1.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.userstories1.R;

/**
 * EditPhotoSetFragments
 * Delete the photo fragment on long press
 * Launched from menu on PhotoGalleryFragment
 */
public class EditPhotoSetFragment extends Fragment {
    private static final String FLICKR_IMAGES_PREF = "FLICKR_IMAGES_PREF";
    public static List<String> selectedImgs;
    public static List<String> toBeSavedImgs;

    private RecyclerView imgsDisplay;

    public static EditPhotoSetFragment newInstance() {
        return new EditPhotoSetFragment();
    }

    public static class customTag {
        boolean isRed;
        String url;
    }

    private class editPhotoHolder extends RecyclerView.ViewHolder {
        public ImageView mItemImageView;

        public editPhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }
    }

    private boolean makeRed(ImageView view) {
        boolean isRed = ((customTag) view.getTag()).isRed;
        if (!isRed) {
            view.setColorFilter(Color.argb(150, 255, 200, 200));
            customTag newTag = (customTag) view.getTag();
            newTag.isRed = true;
            view.setTag(newTag);
            return true;
        } else {
            view.setColorFilter(null);
            return false;
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<editPhotoHolder> {

        @NonNull
        @Override
        public editPhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new EditPhotoSetFragment.editPhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull editPhotoHolder holder, int position) {
            String selectedURL = selectedImgs.get(position);
            Picasso.get().load(selectedURL).into(holder.mItemImageView);
            customTag tag = new customTag();
            tag.isRed = false;
            tag.url = selectedURL;
            holder.mItemImageView.setTag(tag);
            holder.mItemImageView.setColorFilter(null);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = (String) ((customTag) v.getTag()).url;
                    if (makeRed((ImageView) v)) {
                        Toast.makeText(getActivity(), "This image will be removed after you go back!", Toast.LENGTH_SHORT).show();
                        toBeSavedImgs.remove(url);
                    } else {
                        if (!(toBeSavedImgs.contains(url))) {
                            Toast.makeText(getActivity(), "This image will not be removed after you go back!", Toast.LENGTH_SHORT).show();
                            toBeSavedImgs.add(url);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectedImgs.size();
        }


    }

    private void getSavedImages() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        if (!(json.equals(""))) {
            selectedImgs = gson.fromJson(json, type);
        } else {
            selectedImgs = new ArrayList<>();
        }
        toBeSavedImgs = new ArrayList<>(selectedImgs);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editphotoset, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imgsDisplay = getActivity().findViewById(R.id.selectedImages);
        imgsDisplay.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        imgsDisplay.setHasFixedSize(true);
        getSavedImages();
        RecyclerAdapter adapter = new RecyclerAdapter();
        imgsDisplay.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_edit_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toBeSavedImgs);
        editor.putString(FLICKR_IMAGES_PREF, json);
        editor.apply();
        super.onStop();
    }
}

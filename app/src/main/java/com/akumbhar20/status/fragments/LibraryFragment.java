package com.akumbhar20.status.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.akumbhar20.status.R;
import com.akumbhar20.status.activities.UploadActivity;
import com.akumbhar20.status.adapters.Library_Adapter;
import com.akumbhar20.status.models.Video;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Video> video_list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Library_Adapter adapter;

    public LibraryFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_uploads);
        video_list = new ArrayList<Video>();
        adapter = new Library_Adapter(getActivity(), video_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        Query Reference = db.collection("user_uploads").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("videos").orderBy("timestamp", Query.Direction.DESCENDING);
        Reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Video video = doc.getDocument().toObject(Video.class);
                        video_list.add(video);
                        //  adapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });


        adapter.notifyDataSetChanged();





    }


}

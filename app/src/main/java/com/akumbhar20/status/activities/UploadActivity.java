package com.akumbhar20.status.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.akumbhar20.status.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Timestamp;

public class UploadActivity extends AppCompatActivity {
    //for commit changes
    private static final int CHOOSE_VIDEO_REQUEST = 100;
    private Button choose_video;
    ProgressBar progressBar;

    Uri videouri;
    MediaController mediaController;
private VideoView videoView;
FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        storage=FirebaseStorage.getInstance();
        choose_video=(Button)findViewById(R.id.choose_video_btn);
        videoView=(VideoView)findViewById(R.id.videoView);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        choose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choose_video.getText().toString().equalsIgnoreCase("upload") && videouri!=null){

                    StorageReference storageRef = storage.getReference().child("uploaded video/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+System.currentTimeMillis()+".mp4");
                    UploadTask uploadTask=storageRef.putFile(videouri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(UploadActivity.this, "video uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    long progress=taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()*100;
                                    progressBar.setProgress((int) progress);
                                }
                            });
                                   }else {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "select a video"), CHOOSE_VIDEO_REQUEST);
                }
            }


        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_VIDEO_REQUEST && resultCode==RESULT_OK && data!=null){
            videouri= data.getData();
            videoView.setVideoURI(videouri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mediaController=new MediaController(UploadActivity.this);
                            videoView.setMediaController(mediaController);

                            mediaController.setAnchorView(videoView);

                        }
                    });
                }
            });
            choose_video.setText("Upload");
            videoView.start();



        }
    }
}

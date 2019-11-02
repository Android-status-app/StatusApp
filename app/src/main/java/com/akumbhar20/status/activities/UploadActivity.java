package com.akumbhar20.status.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.akumbhar20.status.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    //for commit changes
    private static final int CHOOSE_VIDEO_REQUEST = 100;
    private Button choose_video;
    ProgressBar progressBar;
    TextView ptext,ttext;
    ImageView thumbnail;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    Uri videouri;
    MediaController mediaController;
private VideoView videoView;
FirebaseStorage storage;
EditText title;
    private String thumb_uri = "NULL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        storage=FirebaseStorage.getInstance();
        choose_video=(Button)findViewById(R.id.choose_video_btn);
        videoView=(VideoView)findViewById(R.id.videoView);
        progressBar=findViewById(R.id.progressBar);
        ttext=findViewById(R.id.v_title);
        progressBar.setVisibility(View.GONE);
        progressBar.setMax(100);
        ptext=findViewById(R.id.ptext);
        thumbnail = findViewById(R.id.upload_thumbnail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progressBar.setMin(0);
        }

        choose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choose_video.getText().toString().equalsIgnoreCase("upload") && videouri!=null){
                    title=findViewById(R.id.video_title);
                    if (title.getText().toString().equalsIgnoreCase("")){
                        title.setError("Enter title");
                        title.requestFocus();
                        return;
                    }
                    choose_video.setEnabled(false);
                    ptext.setVisibility(View.VISIBLE);
                    final StorageReference storageRef = storage.getReference().child("uploaded video/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+System.currentTimeMillis()+".mp4");
                    UploadTask uploadTask=storageRef.putFile(videouri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            choose_video.setEnabled(true);
                            choose_video.setText("Done");
                            ptext.setText("success");
                            Toast.makeText(UploadActivity.this, "video uploaded successfully", Toast.LENGTH_SHORT).show();
                            //Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videouri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                           /* if (thumbnail!=null){
                             //   strorethumb(thumbnail);
                            }else{
                                try {
                                   // strorethumb(retriveVideoFrameFromVideo(videouri.getPath()));
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }*/
                           // BitmapDrawable bitmapD = new BitmapDrawable(thumbnail);
                          //  videoView.setBackground(bitmapD);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    choose_video.setText("Choose video");
                                    choose_video.setEnabled(true);
                                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    long progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                    System.out.println("upload progress is :"+progress);
                                    progressBar.setProgress((int) progress);
                                    ptext.setText((int) progress+"%");

                                }
                            });
                    Task<Uri> urltask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return storageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUri = task.getResult().toString();

                                store_video_info(downloadUri);


                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });



                                   }else if(choose_video.getText().toString().equalsIgnoreCase("Done")) {
                    onBackPressed();
                }
                    else{
                        title=findViewById(R.id.video_title);
                        title.setVisibility(View.VISIBLE);
                        ttext.setVisibility(View.VISIBLE);
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "select a video"), CHOOSE_VIDEO_REQUEST);
                    }
                }



        });


    }

    private void store_video_info(final String downloadUri) {
        title=(EditText)findViewById(R.id.video_title);
        final StorageReference storageRef = storage.getReference().child("video thumbnails/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + System.currentTimeMillis() + ".png");
        thumbnail.setDrawingCacheEnabled(true);
        thumbnail.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) thumbnail.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] thumb_data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(thumb_data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadActivity.this, "thumbnail uploaded", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, "thumbnail upload error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        thumb_uri = task.getResult().toString();

                        Map<String, Object> data = new HashMap<>();
                        data.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        data.put("title", title.getText().toString());
                        data.put("thumbnail", thumb_uri);
                        data.put("timestamp", Timestamp.now());
                        data.put("downloaduri", downloadUri);
                        data.put("status", "Approved");
                        //db=FirebaseFirestore.getInstance().document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.collection("user_uploads")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("videos")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(UploadActivity.this, "firestore data Added", Toast.LENGTH_SHORT).show();

                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

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
            Glide.with(UploadActivity.this).load(videouri).into(thumbnail);
            thumbnail.setDrawingCacheEnabled(true);
            videoView.setBackground(thumbnail.getDrawable());




        }

    }

    private void strorethumb(Bitmap thumbnail) {
        FirebaseStorage storage1=FirebaseStorage.getInstance();
        final StorageReference storageRef2 = storage1.getReference().child("uploaded video thumbnails/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+System.currentTimeMillis()+".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef2.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UploadActivity.this, "thumb error :"+exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadActivity.this, "Thumbnail added successfully", Toast.LENGTH_SHORT).show();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(-1,MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


}

package com.example.yazlab3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yazlab3.Kontrol.Internet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button ImageBtn;
    Button CamBtn;
    Button Segman;
    Button Sıkıstırma;
    ImageView ImageView;
    Uri imageUri;
    private StorageReference mStorageRef;
    static final int CAMERA=33;
    static final int SELECT_IMAGE=10;
    static final int MY_CAMERA_PERMISSION_CODE=20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageBtn=findViewById(R.id.button);
        ImageView=findViewById(R.id.imageView);
        Sıkıstırma=findViewById(R.id.button2);
        Segman=findViewById(R.id.button3);
        ImageBtn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent intent =new Intent(Intent.ACTION_GET_CONTENT)  ;
                intent.setType("image/*");
                startActivityForResult(intent ,SELECT_IMAGE);
            }

        });
        CamBtn=(Button)findViewById(R.id.button1);

        CamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);

                    }
                    else
                    {
                        Intent kamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Resim çekme isteği ve activity başlatılıp id'si tanımlandı
                        startActivityForResult(kamera,CAMERA);
                    }
                }


            }
        });
    }





    public void up(View view){
        Intent intent=new Intent(getApplicationContext(),ImageActivity.class);
        startActivity(intent);
        uploadFile();
    }


    public void yeni(View view){

        Intent intent=new Intent(getApplicationContext(),ImageActivity.class);
        startActivity(intent);
    }

    private void uploadFile(){

        if (imageUri!=null){

            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref=mStorageRef.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded"+(int)progress+"%");
                        }
                    });


        }








    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==CAMERA && resultCode==RESULT_OK){
            Bitmap image=(Bitmap)data.getExtras().get("data");//Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
            ImageView resim= (ImageView)findViewById(R.id.imageView);
            resim.setImageBitmap(image);

        }
        if (requestCode==SELECT_IMAGE && resultCode==RESULT_OK){

            imageUri=data.getData();
            ImageView.setImageURI(imageUri);
        }else if (requestCode==SELECT_IMAGE && resultCode==RESULT_CANCELED){
            // Toast.makeText(context:this, text:"İşlem İptal Edildi",Toast.LENGTH_LONG).show());

        }
    }}

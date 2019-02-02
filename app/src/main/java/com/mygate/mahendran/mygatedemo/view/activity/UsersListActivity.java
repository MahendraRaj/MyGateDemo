package com.mygate.mahendran.mygatedemo.view.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mygate.mahendran.mygatedemo.R;
import com.mygate.mahendran.mygatedemo.controller.SimpleOtpGenerator;
import com.mygate.mahendran.mygatedemo.model.dao.RealmUser;
import com.mygate.mahendran.mygatedemo.view.App;
import com.mygate.mahendran.mygatedemo.view.adaptor.UsersListAdaptor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;

public class UsersListActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 200;
    private RecyclerView recyclerView;
   private UsersListAdaptor adaptor;
   private OrderedRealmCollection<RealmUser> users;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(UsersListActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView  = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(UsersListActivity.this));
        queryRealmUsers();
        adaptor = new UsersListAdaptor(UsersListActivity.this,users,true,true);
        recyclerView.setAdapter(adaptor);
        adaptor.getItemCount();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedPermission.with(UsersListActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use Camera\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }

    private void queryRealmUsers() {
         users = App.getInstance().getRealmInstance().where(RealmUser.class).findAll().sort("userName");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            if(resultCode  ==  RESULT_OK) {
                if (data != null) {
                    if(data.getExtras()  != null) {
                        Bitmap image = (Bitmap) data.getExtras().get("data");
                        Log.d("Image",""+ image);
                        saveFileOnThread(image,"User " + adaptor.getItemCount());
                    }

                }

            }
        }
    }

    private void saveFileOnThread(Bitmap image, String userName) {
       Handler  handler = new Handler();
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
               String filename = userName + ".png";
               File sd = Environment.getExternalStorageDirectory();
               File dest = new File(sd, filename);
               try {
                   FileOutputStream out = new FileOutputStream(dest);
                   image.compress(Bitmap.CompressFormat.PNG, 90, out);
                   out.flush();
                   out.close();
               } catch (Exception e) {
                   e.printStackTrace();
               }

               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       //Update UI
                    App.getInstance().getRealmInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmUser user = new RealmUser();
                            user.setUserId(userName);
                            user.setUserName(userName);
                            user.setPasscode(SimpleOtpGenerator.random(6));
                            user.setUserPic(dest.getAbsolutePath());
                            realm.insertOrUpdate(user);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if(adaptor != null) {
                                adaptor.notifyDataSetChanged();
                            }
                        }
                    });

                   }
               });

           }
       };

       Thread  thread  = new Thread(runnable);
       thread.start();
    }


}

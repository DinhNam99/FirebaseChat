package com.dell.firebasechat.view;

import com.dell.firebasechat.model.Message;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dell.firebasechat.R;
import com.dell.firebasechat.adapter.MessageAdapter;
import com.dell.firebasechat.model.User;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int TAKE_PHOTO = 200;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout file;
    LinearLayout photo;
    LinearLayout take_photo;
    CircleImageView imageFriend;
    TextView tvFriend;
    ImageView igSend;
    EditText edMess;
    String hisid;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Uri fileUri;

    List<Message> messageList;
    MessageAdapter adapter;
    RecyclerView rcMess;
    DatabaseReference reference;
    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageFriend = findViewById(R.id.igUserItem);
        tvFriend = findViewById(R.id.tvUsename);
        igSend = findViewById(R.id.igSend);
        edMess = findViewById(R.id.edMess);
        rcMess = findViewById(R.id.rcMess);
        rcMess.setHasFixedSize(true);
        messageList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcMess.setLayoutManager(linearLayoutManager);
        rcMess.scrollToPosition(messageList.size()-1);



        //id friend
        Intent intent = getIntent();
        tvFriend.setText(intent.getStringExtra("UserName")+"");
        hisid = intent.getStringExtra("Uid");

        //diaglog upfile,photo...
        bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
        View view = getLayoutInflater().inflate(R.layout.diaglog_option,null);
        bottomSheetDialog.setContentView(view);


        file = view.findViewById(R.id.File);
        photo = view.findViewById(R.id.Photo);
        take_photo = view.findViewById(R.id.TakePhoto);
        file.setOnClickListener(this);
        photo.setOnClickListener(this);
        take_photo.setOnClickListener(this);

        ImageView igOption = findViewById(R.id.igOption);
        igOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        //firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        igSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edMess.getText().toString();
                if(TextUtils.isEmpty(mess)){
                    Toast.makeText(ChatActivity.this, "You can't send empty message!!!", Toast.LENGTH_SHORT).show();
                }else{
                    sendMess(firebaseUser.getUid(),hisid,mess);
                }
                edMess.setText("");
            }
        });
        readMessage(firebaseUser.getUid(),hisid);
        seenMess(edMess.getText().toString());


    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.File:
                Toast.makeText(this, "File",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Photo:
                chooseImage();
                Toast.makeText(this, "Photo",Toast.LENGTH_SHORT).show();
                break;
            case R.id.TakePhoto:
                takePhoto();
                Toast.makeText(this, "Take photo",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void chooseImage() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void takePhoto(){
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cInt,TAKE_PHOTO);
    }

    private void sendMess(String sender, final String receiver, final String mess){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String timeLive = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",mess);
        hashMap.put("imageMess","");
        hashMap.put("isSeen", false);
        hashMap.put("time",timeLive);

        reference.child("Chats").push().setValue(hashMap);


    }

    private void readMessage(final String idSender, final String idReceiver) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message chats = snapshot.getValue(Message.class);
                    if (chats.getSender().equals(idSender) && chats.getReceiver().equals(idReceiver) || chats.getSender().equals(idReceiver) && chats.getReceiver().equals(idSender)) {
                        messageList.add(chats);
                    }
                }
                adapter = new MessageAdapter(ChatActivity.this, messageList);
                rcMess.setAdapter(adapter);
                rcMess.scrollToPosition(messageList.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void seenMess(final String message){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(message.getReceiver().equals(firebaseUser.getUid())&& message.getSender().equals(hisid)){
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            fileUri = data.getData();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Do you want to send image ?");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadImage();
                        adapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                        bottomSheetDialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            fileUri = data.getData();
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setTitle("Do you want to send image ?");
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    uploadImage();
                    dialogInterface.dismiss();
                    bottomSheetDialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void uploadImage() {
        if(fileUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putFile(fileUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        String timeLive = String.valueOf(System.currentTimeMillis());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender",firebaseUser.getUid());
                        hashMap.put("receiver", hisid);
                        hashMap.put("isSeen", false);
                        hashMap.put("time",timeLive);
                        hashMap.put("imageMess",String.valueOf(downloadUri));

                        reference.child("Chats").push().setValue(hashMap);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }


}

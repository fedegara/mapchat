package com.example.federicogarateguy.mapchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ArrayList chatMsgs = new ArrayList<String>();
    private Boolean isSecondPlane = false;
    public static final int LOGIN_CODE=200;


    NotificationManager manager;
    Notification myNotication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        String user_data = sp.getString("user_data", null);
        if (user_data == null) {
            startActivityForResult(new Intent(this, Login.class),LOGIN_CODE);
        }else{
            listenerDatabase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        listenerDatabase();
    }


    public void listenerDatabase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        ref.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MapChatMessage msg = dataSnapshot.getValue(MapChatMessage.class);
                chatMsgs.add(msg.getEmail() + ": " + msg.text);
                loadMessage();

                if (isSecondPlane == true) {
                    Log.d("Notificacion", "muestro");
                    notificacion(msg.getEmail() + ": " + msg.text);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MapChatMessage msg = dataSnapshot.getValue(MapChatMessage.class);
                chatMsgs.add(msg.getEmail() + ": " + msg.text);
                loadMessage();

                if (isSecondPlane == true) {
                    Log.d("Notificacion", "muestro");
                    notificacion(msg.getEmail() + ": " + msg.text);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadMessage() {
        ListView lv = (ListView) findViewById(R.id.lvMensajes);
        ArrayAdapter adaptador = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, chatMsgs);
        lv.setAdapter(adaptador);
    }

    public void sendMessage(View v) {
        String message = ((EditText) findViewById(R.id.message)).getText().toString();
        if (message != "") {
            MapChatMessage new_message = new MapChatMessage(message, auth.getCurrentUser());
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("messages");
            mRef.child(new_message.getFirebase_uid() + "-" + System.currentTimeMillis()).setValue(new_message);
        }
    }

    public void goToMap(View v) {
        startActivity(new Intent(this, Maps.class));
    }

    public void notificacion(String message) {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(MainActivity.this);

        builder.setAutoCancel(false);
        builder.setContentTitle("Mapchat");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.unnamed);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(false);
        builder.setSubText(message);
        myNotication = builder.build();
        manager.notify(11, myNotication);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("second plane", "me fui a segundo plano");
        isSecondPlane = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("fisrtplane", "volvi a ver la applicacion");
        isSecondPlane = false;
    }
}

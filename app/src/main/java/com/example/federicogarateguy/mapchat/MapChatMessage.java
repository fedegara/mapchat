package com.example.federicogarateguy.mapchat;


import com.google.firebase.auth.FirebaseUser;

/**
 * Created by federicogarateguy on 16/11/16.
 */

public class MapChatMessage {

    public String text;
    public String email;
    public String firebase_uid;
    public String displayName;

    public MapChatMessage(String text, FirebaseUser user) {
        this.text = text;
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.firebase_uid = user.getUid();
    }

    public MapChatMessage() {
    }

    public String getText() {
        return text;
    }

    public String getEmail() {
        return email;
    }

    public String getFirebase_uid() {
        return firebase_uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "Text:" + this.text + " | User:" + this.displayName ;
    }
}

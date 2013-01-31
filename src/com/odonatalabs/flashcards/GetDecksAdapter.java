package com.odonatalabs.flashcards;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GetDecksAdapter {
    private static final String TAG = "GetDecksAdapter";

    public interface GetDecksClient {
        public void decksAvailable(ArrayList<Deck> decks);
        
        public void error(String errorMessage);
    }

    public static void getDecks(final GetDecksClient client) {
        ParseQuery query = new ParseQuery("Deck");
        query.findInBackground(new FindCallback() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<Deck> decks = new ArrayList<Deck>();
                    for (ParseObject o : objects) {
                        String objectId = o.getObjectId();
                        String name = o.getString("name");
                        decks.add(new Deck(objectId, name));
                    }

                    client.decksAvailable(decks);
                } else {
                    Log.e(TAG, "Crap...", e);
                    client.error(e.getMessage());
                }
            }
        });
    }
}

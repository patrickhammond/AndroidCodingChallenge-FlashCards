package com.odonatalabs.flashcards;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GetCardsAdapter {
    private static final String TAG = "GetCardsAdapter";

    public interface GetCardsClient {
        public void cardsAvailable(ArrayList<Card> cards);
        
        public void error(String errorMessage);
    }

    public static void getCards(Deck deck, final GetCardsClient client) {
        ParseQuery query = new ParseQuery("Card");
        query.whereEqualTo("deckObjectId", deck.objectId);
        query.findInBackground(new FindCallback() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<Card> cards = new ArrayList<Card>();
                    for (ParseObject o : objects) {
                        String question = o.getString("question");
                        String answer = o.getString("answer");
                        cards.add(new Card(question, answer));
                    }
                    client.cardsAvailable(cards);
                } else {
                    Log.e(TAG, "Crap...", e);
                    client.error(e.getMessage());
                }
            }
        });
    }
}

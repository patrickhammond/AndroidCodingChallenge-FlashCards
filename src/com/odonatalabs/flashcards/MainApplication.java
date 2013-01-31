package com.odonatalabs.flashcards;

import android.app.Application;

import com.parse.Parse;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "dNCXVHDOIFcEs6aNDjYObGw5yNBvtSwHL5mLkF8H", "E3qQhJDTbSyBP8WDgbJHmb3xRN0oEPW8aJPui2Sy");

//        GetDecksAdapter.getDecks(new GetDecksClient() {
//            @Override
//            public void decksAvailable(ArrayList<Deck> decks) {
//                for (Deck deck : decks) {
//                    Log.v("DEBUG", "Deck: " + deck.name);
//                    GetCardsAdapter.getCards(deck, new GetCardsClient() {
//                        @Override
//                        public void cardsAvailable(ArrayList<Card> cards) {
//                            for (Card card : cards) {
//                                Log.v("DEBUG", " Q: " + card.question + " A: " + card.answer);
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }
}

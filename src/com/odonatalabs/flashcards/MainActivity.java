package com.odonatalabs.flashcards;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.odonatalabs.flashcards.GetCardsAdapter.GetCardsClient;
import com.odonatalabs.flashcards.GetDecksAdapter.GetDecksClient;

public class MainActivity extends FragmentActivity implements OnNavigationListener, GetDecksClient, GetCardsClient {

    private ViewPager viewPager;
    private CardsPagerAdapter pagerAdapter;

    private ArrayList<Deck> decks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new CardsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        setProgressBarIndeterminateVisibility(true);
        GetDecksAdapter.getDecks(this);
    }

    @Override
    public void decksAvailable(ArrayList<Deck> decks) {
        setProgressBarIndeterminateVisibility(false);
        this.decks = decks;
        getActionBar().setListNavigationCallbacks(new DecksListArrayAdapter(this, decks), this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (decks != null) {
            Deck deck = decks.get(itemPosition);

            setProgressBarIndeterminateVisibility(true);
            GetCardsAdapter.getCards(deck, this);
        }

        return true;
    }

    @Override
    public void cardsAvailable(ArrayList<Card> cards) {
        setProgressBarIndeterminateVisibility(false);
        pagerAdapter.changeCards(cards);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void error(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private class CardsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Card> cards;

        public CardsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void changeCards(ArrayList<Card> cards) {

            this.cards = cards;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            Card card = cards.get(position);

            Fragment fragment = new CardFragment();
            Bundle args = new Bundle();
            args.putInt(CardFragment.ARG_POSITION, position);
            args.putString(CardFragment.ARG_QUESTION, card.question);
            args.putString(CardFragment.ARG_ANSWER, card.answer);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            if (cards == null) {
                return 0;
            }

            return cards.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (cards == null) {
                return "";
            }

            return "Question " + (position + 1);
        }
    }

    private static class CardFragment extends Fragment {

        public static final String ARG_QUESTION = "question";
        public static final String ARG_ANSWER = "answer";
        public static final String ARG_POSITION = "position";

        public CardFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.question_answer, null);
            TextView question = (TextView) view.findViewById(R.id.question);
            TextView answer = (TextView) view.findViewById(R.id.answer);
            final View answerCard = view.findViewById(R.id.answer_card);
            final View blindCard = view.findViewById(R.id.answer_blind_card);

            Button previous = (Button) view.findViewById(R.id.previous);
            Button next = (Button) view.findViewById(R.id.next);

            question.setText(getArguments().getString(ARG_QUESTION));
            answer.setText(getArguments().getString(ARG_ANSWER));

            blindCard.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    flip(answerCard, blindCard);
                }
            });

            int position = getArguments().getInt(ARG_POSITION);
            if (position == 0) {
                previous.setVisibility(View.GONE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }

            return view;
        }

        private void flip(final View answer, final View blind) {
            Interpolator accelerator = new AccelerateInterpolator();
            Interpolator decelerator = new DecelerateInterpolator();

            ObjectAnimator visToInvis = ObjectAnimator.ofFloat(blind, "rotationY", 0f, 90f);
            visToInvis.setDuration(500);
            visToInvis.setInterpolator(accelerator);
            final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(answer, "rotationY", -90f, 0f);
            invisToVis.setDuration(500);
            invisToVis.setInterpolator(decelerator);
            visToInvis.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator anim) {
                    blind.setVisibility(View.GONE);
                    invisToVis.start();
                    answer.setVisibility(View.VISIBLE);
                }
            });
            visToInvis.start();
        }
    }

    private class DecksListArrayAdapter extends ArrayAdapter<Deck> {
        public DecksListArrayAdapter(Context context, ArrayList<Deck> decks) {
            super(context, 0, decks);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);
            }

            Deck deck = getItem(position);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(deck.name);

            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), android.R.layout.simple_spinner_item, null);
            }

            Deck deck = getItem(position);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(deck.name);

            return convertView;
        }
    }
}

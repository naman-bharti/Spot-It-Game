package ca.cmpt276.userstories1.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ca.cmpt276.userstories1.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * RankConfigFragment
 * Changes the current scoreboard displayed in Ranking activity
 */

public class RankingConfigFragment extends DialogFragment {
    public static final String RANK_CARDS_POS_PREF = "RankCardsTextPref";
    public static final String RANK_CARDS_NUM_PREF = "RankCardsNumPref";
    public static final String RANK_ORDER_POS_PREF = "RankOrderTextPref";
    public static final String RANK_ORDER_NUM_PREF = "RankOrderNumPref";

    private Spinner cards_spinner;
    private Spinner order_spinner;
    private Button finish;

    public static RankingConfigFragment newInstance(String title) {
        RankingConfigFragment frag = new RankingConfigFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    private void setSpinners() {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.gameOrders, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order_spinner.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.gameTime, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cards_spinner.setAdapter(adapter);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(RANK_ORDER_POS_PREF, MODE_PRIVATE);
        int spinnerPos = sharedPref.getInt(RANK_ORDER_POS_PREF, -1);
        if (spinnerPos != -1) {
            order_spinner.setSelection(spinnerPos);
        }

        sharedPref = getActivity().getSharedPreferences(RANK_CARDS_POS_PREF, MODE_PRIVATE);
        spinnerPos = sharedPref.getInt(RANK_CARDS_POS_PREF, -1);
        if (spinnerPos != -1) {
            cards_spinner.setSelection(spinnerPos);
        } else {
            cards_spinner.setSelection(4);
        }
    }

    private void setSpinnerListeners() {
        cards_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(RANK_CARDS_POS_PREF, MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt(RANK_CARDS_POS_PREF, position);
                prefEditor.apply();

                sharedPref = getActivity().getSharedPreferences(RANK_CARDS_NUM_PREF, MODE_PRIVATE);
                prefEditor = sharedPref.edit();
                int cardsNumber = 5;
                switch (position) {
                    case 0:
                        cardsNumber = 5;
                        break;
                    case 1:
                        cardsNumber = 10;
                        break;
                    case 2:
                        cardsNumber = 15;
                        break;
                    case 3:
                        cardsNumber = 20;
                        break;
                    case 4:
                        // Leave zero for all cards
                        cardsNumber = 0;
                }
                prefEditor.putInt(RANK_CARDS_NUM_PREF, cardsNumber);
                prefEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        order_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(RANK_ORDER_POS_PREF, MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt(RANK_ORDER_POS_PREF, position);
                prefEditor.apply();

                sharedPref = getActivity().getSharedPreferences(RANK_ORDER_NUM_PREF, MODE_PRIVATE);
                prefEditor = sharedPref.edit();
                int orderNumber = 3;
                switch (position) {
                    case 0:
                        orderNumber = 3;
                        break;
                    case 1:
                        orderNumber = 4;
                        break;
                    case 2:
                        orderNumber = 6;
                }
                prefEditor.putInt(RANK_ORDER_NUM_PREF, orderNumber);
                prefEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setButton() {
        finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_scoreboardconfig, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cards_spinner = getView().findViewById(R.id.rankCardsSpinner);
        order_spinner = getView().findViewById(R.id.rankOrderSpinner);
        finish = getView().findViewById(R.id.saveButton);
        setButton();
        setSpinners();
        setSpinnerListeners();
    }

    @Override
    public void onStop() {
        getActivity().recreate();
        super.onStop();
    }
}


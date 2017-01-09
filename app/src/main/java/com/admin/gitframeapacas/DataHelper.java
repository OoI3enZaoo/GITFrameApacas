package com.admin.gitframeapacas;


import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {


    private static final String COLORS_FILE_NAME = "colors.json";


    //    private static List<DistrictSuggestion> sDistrictSuggestions = new ArrayList<>((benkung()));
    public static List<DistrictSuggestion> sDistrictSuggestions = new ArrayList<>();

    private static List<DistrictSuggestion> addListHistory = new ArrayList<>();

    public static List<DistrictSuggestion> getHistory(Context context, int count) {

        List<DistrictSuggestion> suggestionList = new ArrayList<>();
        DistrictSuggestion districtSuggestion;


        for (int j = 0; j < addListHistory.size(); j++) {
            districtSuggestion = addListHistory.get(j);
            if (!districtSuggestion.toString().equals("")) {
                districtSuggestion.setIsHistory(true);
                suggestionList.add(districtSuggestion);
                if (suggestionList.size() == count) {
                    addListHistory.clear();
                    return suggestionList;

                }
            }
        }
        for (int i = 0; i < sDistrictSuggestions.size(); i++) {
            districtSuggestion = sDistrictSuggestions.get(i);
            suggestionList.add(districtSuggestion);
            if (suggestionList.size() == count) {
                return suggestionList;
            }

        }
        return suggestionList;
    }

    public static void addHistroy(DistrictSuggestion result) {
        addListHistory.add(result);


    }

    public static void resetSuggestionsHistory() {
        for (DistrictSuggestion districtSuggestion : sDistrictSuggestions) {
            districtSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<DistrictSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (DistrictSuggestion suggestion : sDistrictSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<DistrictSuggestion>() {
                    @Override
                    public int compare(DistrictSuggestion lhs, DistrictSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DistrictSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public interface OnFindSuggestionsListener {
        void onResults(List<DistrictSuggestion> results);
    }


}
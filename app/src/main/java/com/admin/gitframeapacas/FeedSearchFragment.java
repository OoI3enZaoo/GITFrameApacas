package com.admin.gitframeapacas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;

import java.util.List;

public class FeedSearchFragment extends Fragment {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    static String TAG = "BlankFragment";
    //private static ProgressDialog mProgressDialog;
    FloatingSearchView mSearchDistrict;
    FloatingSearchView mSearchNavigate;
    private ColorDrawable mDimDrawable;
    private boolean mIsDarkSearchTheme = false;
    private String mLastQuery = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frame_search, container, false);

        mSearchDistrict = (FloatingSearchView) v.findViewById(R.id.search_district);
        mSearchNavigate = (FloatingSearchView) v.findViewById(R.id.search_navigate);


        //searchDistrict.attachNavigationDrawerToMenuButton(mDrawerLayout);
        //DrawerLayout draw = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        //searchDistrict.attachNavigationDrawerToMenuButton(draw);


       /* mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();*/

        //new JSONFeedLogTask2().execute();

        setupFloatingSearch();


        return v;

    }

    private void setupFloatingSearch() {
        mSearchDistrict.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchDistrict.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchDistrict.showProgress();

                    //simulates a query call to a data source
                    //with a new query.

                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<DistrictSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchDistrict.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchDistrict.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchDistrict.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                Log.d(TAG, "onSearchAction()");
                mLastQuery = query;

            }
        });
        mSearchDistrict.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                Log.d(TAG, "onFocus()");
                mSearchDistrict.swapSuggestions(DataHelper.getHistory(getActivity(), 4));
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchDistrict.setSearchBarTitle("");


                Log.d(TAG, "onFocusCleared()");
                mSearchDistrict.setSearchText("");
                mSearchDistrict.setSearchFocused(false);
                if (!mLastQuery.equals("")) {

                    DataHelper.addHistroy(new DistrictSuggestion(mLastQuery));
                    mLastQuery.equals("");
                    Intent intent = new Intent(getActivity(), DistrictActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("district", mLastQuery);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });


        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchDistrict.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Toast.makeText(getActivity(), "Back", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onHomeClicked()");
            }
        });
        mSearchDistrict.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {


                DistrictSuggestion districtSuggestion = (DistrictSuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";

                if (districtSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }
              /*  textView.setTextColor(Color.parseColor(textColor));
                String text = districtSuggestion.getBody()
                        .replaceFirst(searchDistrict.getQuery(),
                                "<font color=\"" + textLight + "\">" + searchDistrict.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));*/
            }

        });

        mSearchDistrict.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.iconProfile) {
                    //mIsDarkSearchTheme = true;
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);

                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


        mSearchNavigate.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchNavigate.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchNavigate.showProgress();

                    //simulates a query call to a data source
                    //with a new query.

                    DataHelper.findSuggestions(getActivity(), newQuery, 3,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<DistrictSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchNavigate.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchNavigate.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchNavigate.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                Log.d(TAG, "onSearchAction()");
                mLastQuery = query;

            }
        });
        mSearchNavigate.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                Log.d(TAG, "onFocus()");
                mSearchNavigate.swapSuggestions(DataHelper.getHistory(getActivity(), 1));
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchNavigate.setSearchBarTitle("");


                Log.d(TAG, "onFocusCleared()");
                mSearchNavigate.setSearchText("");
                mSearchNavigate.setSearchFocused(false);
                Toast.makeText(getActivity(), "Navigate to:" + mLastQuery, Toast.LENGTH_SHORT).show();
                /*if (!mLastQuery.equals("")) {

                    DataHelper.addHistroy(new DistrictSuggestion(mLastQuery));
                    mLastQuery.equals("");
                    Intent intent = new Intent(getActivity(), DistrictActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("district", mLastQuery);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }*/

            }
        });


        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchNavigate.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Toast.makeText(getActivity(), "Back", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onHomeClicked()");
            }
        });
        mSearchNavigate.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {


                DistrictSuggestion districtSuggestion = (DistrictSuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";

                if (districtSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }
              /*  textView.setTextColor(Color.parseColor(textColor));
                String text = districtSuggestion.getBody()
                        .replaceFirst(searchDistrict.getQuery(),
                                "<font color=\"" + textLight + "\">" + searchDistrict.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));*/
            }

        });

        mSearchNavigate.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.iconProfile) {
                    //mIsDarkSearchTheme = true;
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);

                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean onActivityBackPress() {
        //if searchDistrict.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if searchDistrict.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        return mSearchDistrict.setSearchFocused(false);
    }
/*
    private static class JSONFeedLogTask2 extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "JSONFeed");


        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "doinbackground");
            Request request = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th:8161/aparcas/GetDistrict2.jsp")
                    .build();

            try {

                int count = 1;
                int rount2 = 0;
                Response response;
                response = client.newCall(request).execute();
                String result2 = response.body().string();
                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<DistrictResponse>>() {
                }.getType();
                Collection<DistrictResponse> enums = gson.fromJson(result2, collectionType);
                DistrictResponse[] result = enums.toArray(new DistrictResponse[enums.size()]);

                //Log.i("MYRESULT: ", String.valueOf(result));
                String[] checkSame = new String[result.length + 2];

                for (int i = 0; i < result.length; i++) {
                    String DName = result[i].getName();
                    for (int j = 0; j < checkSame.length; j++) {
                        if (checkSame[j] != null) {
                            if (checkSame[j].equals(DName)) {
                                count = 0;
                                break;
                                //   Log.i("resultben", "count == 0: " + DName);
                            }
                        }
                    }
                    if (count == 1) {

                        sDistrictSuggestions.add(rount2, new DistrictSuggestion(DName + ""));
                        //  Log.i("resultben", "count == 1: " + DName);
                        checkSame[i] = DName;
                        rount2++;
                    }
                    count = 1;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();

        }


    }*/
}

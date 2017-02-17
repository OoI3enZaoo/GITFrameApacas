package com.admin.gitframeapacas.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

import com.admin.gitframeapacas.Data.DistrictResponse;
import com.admin.gitframeapacas.DataHelper;
import com.admin.gitframeapacas.DistrictSuggestion;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBGrid;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.admin.gitframeapacas.Views.DistrictActivity;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.admin.gitframeapacas.DataHelper.sDistrictSuggestions;

public class FeedSearchFragment extends Fragment {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    static String TAG = "BlankFragment";
    private static ProgressDialog dialog;
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
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...2");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new JSONFeedLogTask2(getActivity()).execute();

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
                    DBGrid dbGrid = new DBGrid(getContext());
                    String scode = dbGrid.getSCode(mLastQuery);
                    bundle.putString("scode", scode);
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

                /*if (item.getItemId() == R.id.iconProfile) {
                    //mIsDarkSearchTheme = true;
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);

                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }*/

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

                /*if (item.getItemId() == R.id.iconProfile) {
                    //mIsDarkSearchTheme = true;
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);

                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }*/

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

    private static class JSONFeedLogTask2 extends AsyncTask<String, Void, String> {

        private Context mContext;

        public JSONFeedLogTask2(Context context) {
            mContext = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "JSONFeed");


        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "doinbackground");
            DBUser dbUser = new DBUser(mContext);
            DBGrid dbGrid = new DBGrid(mContext);
            int gridstatus = dbUser.getGrid();
            Log.i("Searchs", "" + gridstatus);
            if (gridstatus == 0) {

                Request request = new Request.Builder()
                        .url("http://sysnet.utcc.ac.th/aparcas/api/districtForAndroid.jsp")
                        .build();
                try {
                    int rount2 = 0;
                    Response response;
                    response = client.newCall(request).execute();
                    String result2 = response.body().string();
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<DistrictResponse>>() {
                    }.getType();
                    Collection<DistrictResponse> enums = gson.fromJson(result2, collectionType);
                    DistrictResponse[] result = enums.toArray(new DistrictResponse[enums.size()]);

                    for (int i = 0; i < result.length; i++) {
                        String sName = result[i].getSname();
                            sDistrictSuggestions.add(rount2, new DistrictSuggestion(sName + ""));
                            String scode = result[i].getScode();
                            String sname = result[i].getSname();
                            String dcode = result[i].getDcode();
                            String dname = result[i].getDname();
                            String pcode = result[i].getPcode();
                            String pname = result[i].getPname();
                            Log.i("griddata2", dbGrid.insertData(Integer.parseInt(scode), sname, Integer.parseInt(dcode), dname, Integer.parseInt(pcode), pname) + "");
                            Log.i("griddata", "SCODE: " + scode);
                            Log.i("griddata", "sname: " + sname);
                            Log.i("griddata", "dcode: " + dcode);
                            Log.i("griddata", "dname: " + dname);
                            Log.i("griddata", "pcode: " + pcode);
                            Log.i("griddata", "pname: " + pname);
                            Log.i("griddata", "number of rows: " + dbGrid.numberOfRows());
                        }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                dbUser.updateGrid(1);
            } else {
                Cursor res = dbGrid.getAllData();
                if (res.getCount() == 0) {
                    Log.i("griddata", "Nothing found");
                } else {

                    while (res.moveToNext()) {
                        int rount = 0;
                        String sName = res.getString(1);
                        sDistrictSuggestions.add(rount, new DistrictSuggestion(sName + ""));
                        rount++;
                    }

                }




            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

        }


    }
}

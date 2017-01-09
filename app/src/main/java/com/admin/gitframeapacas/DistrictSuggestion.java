package com.admin.gitframeapacas;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DistrictSuggestion implements SearchSuggestion {

    public static final Creator<DistrictSuggestion> CREATOR = new Creator<DistrictSuggestion>() {
        @Override
        public DistrictSuggestion createFromParcel(Parcel in) {
            return new DistrictSuggestion(in);
        }

        @Override
        public DistrictSuggestion[] newArray(int size) {
            return new DistrictSuggestion[size];
        }
    };
    private String mDistrictName;
    private boolean mIsHistory = false;

    public DistrictSuggestion(String suggestion) {
        this.mDistrictName = suggestion.toLowerCase();
    }

    public DistrictSuggestion(Parcel source) {
        this.mDistrictName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    @Override
    public String getBody() {
        return mDistrictName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDistrictName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
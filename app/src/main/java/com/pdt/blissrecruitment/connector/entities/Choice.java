
package com.pdt.blissrecruitment.connector.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Choice implements Parcelable {

    @SerializedName("choice")
    @Expose
    private String choice;
    @SerializedName("votes")
    @Expose
    private Integer votes;

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.choice);
        dest.writeValue(this.votes);
    }

    public Choice() {
    }

    protected Choice(Parcel in) {
        this.choice = in.readString();
        this.votes = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Choice> CREATOR = new Parcelable.Creator<Choice>() {
        @Override
        public Choice createFromParcel(Parcel source) {
            return new Choice(source);
        }

        @Override
        public Choice[] newArray(int size) {
            return new Choice[size];
        }
    };
}

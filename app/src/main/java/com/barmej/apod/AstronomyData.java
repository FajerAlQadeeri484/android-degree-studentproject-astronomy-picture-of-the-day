package com.barmej.apod;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class AstronomyData implements Parcelable {

    private String title;
    private String explanation;
    private String mediaType;
    private String url;


    public AstronomyData(JSONObject jsonObject) throws JSONException{
            this.title = jsonObject.getString("title");
            this.explanation = jsonObject.getString("explanation");
            this.mediaType = jsonObject.getString("media_type");
            this.url = jsonObject.getString("url");
    }

    protected AstronomyData(Parcel in) {
        title = in.readString();
        explanation = in.readString();
        mediaType = in.readString();
        url = in.readString();
    }

    public static final Creator<AstronomyData> CREATOR = new Creator<AstronomyData>() {
        @Override
        public AstronomyData createFromParcel(Parcel in) {
            return new AstronomyData(in);
        }

        @Override
        public AstronomyData[] newArray(int size) {
            return new AstronomyData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(explanation);
        parcel.writeString(mediaType);
        parcel.writeString(url);
    }
}

package com.smurfee.android.emessel.recyclerview;

/**
 * Created by YekHong on 27/10/2015.
 */
public class MSLRowView {

    private long mId;
    private String mItem;
    private boolean mChecked;

    public MSLRowView(long id, String item){
        mId = id;
        mItem = item;
    }

    public long getId() {
        return mId;
    }

    public String getItem() {
        return mItem;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }
}

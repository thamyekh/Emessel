package com.smurfee.android.emessel.recyclerview;

import java.math.BigDecimal;

/**
 * Created by YekHong on 27/10/2015.
 */
public class MSLRowView {

    private long mId;
    private String mLabel;
    private String mNote;
    private BigDecimal mPrice;
    private boolean mChecked;

    public MSLRowView(long id, String label) {
        mId = id;
        mLabel = label;
    }

    public long getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        // input validation
        if (price.equals("")) return;
        mPrice = new BigDecimal(price);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }
}

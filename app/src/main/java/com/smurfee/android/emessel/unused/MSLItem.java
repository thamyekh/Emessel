package com.smurfee.android.emessel.unused;

/**
 * Created by smurfee on 20/09/2015.
 */
public class MSLItem {

    private long id;
    private String item;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return item;
    }
}

package com.smurfee.android.emessel.swipe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.pricefinder.PriceFinder;
import com.smurfee.android.emessel.recyclerview.MSLViewAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by Tham on 25/12/2017.
 */

public class MSLCallback extends ItemTouchHelper.Callback {

    private Paint paint = new Paint();
    private MSLViewAdapter mAdapter;

    public MSLCallback(MSLViewAdapter Adapter) {
        mAdapter = Adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.START) {
            // Execute Price Finder
            TextView price = viewHolder.itemView.findViewById(R.id.edit_price);
            String label = ((TextView) viewHolder.itemView.findViewById(R.id.label)).getText().toString();
            WeakReference<Context> refContext = new WeakReference<>(mAdapter.getContext());
            WeakReference<TextView> refPrice = new WeakReference<>(price);
            new PriceFinder(refContext, refPrice, viewHolder.getAdapterPosition()).execute(label);
        } else {
            // Cycle Through Priority
            boolean newPriority = mAdapter.changePriority(viewHolder.getAdapterPosition());
            //change tag
            ImageView icon = viewHolder.itemView.findViewById(R.id.icon);
            if (newPriority)
                icon.setTag("ic_priority_red_300_48dp");
            else icon.setTag("ic_priority_light_blue_300_48dp");
            Log.d("priority",String.valueOf(newPriority));

            TextView label = viewHolder.itemView.findViewById(R.id.label);
            ((EditText) viewHolder.itemView.findViewById(R.id.edit_label)).setText(label.getText());
            Button btnDone = viewHolder.itemView.findViewById(R.id.done);
            btnDone.callOnClick();
        }
        mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            Bitmap icon;
            float left, top, right, bottom;
            Resources resource = mAdapter.getContext().getResources();
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            if (dX > 0) {
                paint.setColor(Color.parseColor("#E5E5E5"));
                paint.setTextSize((float) 50);
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, paint);
                paint.setColor(Color.parseColor("#388E3C"));
                c.drawText("Priority Changed", (float) itemView.getLeft() + width, (float) itemView.getTop() + 2 * width, paint);
//                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
//                c.drawBitmap(icon,null,icon_dest,paint);
            } else {
                left = (float) itemView.getRight() + dX;
                top = (float) itemView.getTop();
                right = (float) itemView.getRight();
                bottom = (float) itemView.getBottom();
                paint.setColor(ResourcesCompat.getColor(resource, R.color.accent, null));

                RectF background = new RectF(left, top, right, bottom);
                c.drawRect(background, paint);


                left = (float) itemView.getRight() - 2 * width;
                top = (float) itemView.getTop() + width;
                right = (float) itemView.getRight() - width;
                bottom = (float) itemView.getBottom() - width;

                Drawable d = resource.getDrawable(R.drawable.ic_swipe_left);
                d.setBounds((int) left, (int) top, (int) right, (int) bottom);
                d.draw(c);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        // TODO: Set to false if row is expanded
        return true;
    }
}

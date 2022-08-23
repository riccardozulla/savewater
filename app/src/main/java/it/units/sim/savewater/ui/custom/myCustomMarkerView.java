package it.units.sim.savewater.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Date;
import java.util.Locale;

import it.units.sim.savewater.R;

/**
 * Custom implementation of the MarkerView.
 */
@SuppressLint("ViewConstructor")
public class myCustomMarkerView extends MarkerView {


    private static final String TAG = "myCustomMarkerView";
    private final TextView markerTime;
    private final TextView markerIncrement;
    private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public myCustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        markerTime = findViewById(R.id.marker_time);
        markerIncrement = findViewById(R.id.marker_increment);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerTime.setText(mFormat.format(new Date((long) e.getX())));
        markerIncrement.setText((int) e.getY() + R.string.liters_abbr);

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth()), -getHeight() / 2f);
    }
}

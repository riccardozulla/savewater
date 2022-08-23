package it.units.sim.savewater.ui.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentLineChartBinding;
import it.units.sim.savewater.model.Utility;
import it.units.sim.savewater.ui.custom.myCustomMarkerView;

public class LineChartFragment extends SharingChartFragment<LineChart> implements OnChartValueSelectedListener {

    private static final String TAG = "LineChartFragment";
    private FragmentLineChartBinding binding;
    private XAxis xAxis;
    private YAxis yAxis;
    private DashboardViewModel dashboardViewModel;

    @Override
    protected View drawChart(@NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        binding = FragmentLineChartBinding.inflate(inflater, null, false);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        dashboardViewModel.getTargetWaterConsumption().observe(getViewLifecycleOwner(), this::updateTarget);
        dashboardViewModel.getDailyWaterConsumption().observe(getViewLifecycleOwner(), this::updateData);

        chart = binding.lineCharDailyProgress;
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);

        myCustomMarkerView mv = new myCustomMarkerView(chart.getContext(), R.layout.marker_view);
        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(ColorTemplate.getHoloBlue());
        yAxis.setDrawGridLines(true);
        yAxis.setGranularityEnabled(true);
        yAxis.setTextColor(Color.rgb(255, 192, 56));
        yAxis.setGridColor(Color.WHITE);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        chart.animateX(1500);
        Legend legend = chart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.LINE);

        return binding.getRoot();
    }


    private void updateData(List<DocumentSnapshot> documentSnapshots) {
        ArrayList<Entry> values = new ArrayList<>();

        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        int result = 0;
        for (DocumentSnapshot doc : documentSnapshots) {
            Utility utility = doc.toObject(Utility.class);
            result += utility.getWaterConsumption();
            long mills = utility.getTimestamp().toDate().getTime();
            values.add(new Entry(mills, result));
            if (mills < minTime) {
                minTime = mills;
            } else if (mills > maxTime) {
                maxTime = mills;
            }
        }
        xAxis.setAxisMinimum(minTime - TimeUnit.MINUTES.toMillis(30));
        xAxis.setAxisMaximum(maxTime + TimeUnit.MINUTES.toMillis(30));

        LineDataSet dataSet = new LineDataSet(values, "Daily water consumption");
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();
        chart.zoomToCenter(.5f, .5f);

    }

    private void updateTarget(Integer integer) {
        // Remove old Limit Lines
        yAxis.removeAllLimitLines();
        // Create Limit Lines
        LimitLine target = new LimitLine(integer.floatValue(), "Upper Limit");
        target.setLineWidth(4f);
        target.enableDashedLine(10f, 10f, 0f);
        target.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        target.setTextSize(10f);
        target.setTypeface(Typeface.DEFAULT);
        // add limit lines
        yAxis.addLimitLine(target);
        // refresh chart view
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
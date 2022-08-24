package it.units.sim.savewater.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.savewater.databinding.FragmentBarChartBinding;
import it.units.sim.savewater.model.Utility;


public class BarChartFragment extends SharingChartFragment<BarChart> {

    private static final String TAG = "BarChartFragment";
    private FragmentBarChartBinding binding;
    private DashboardViewModel dashboardViewModel;

    @Override
    protected View drawChart(@NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {

        binding = FragmentBarChartBinding.inflate(inflater, null, false);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        dashboardViewModel.getDailyWaterConsumption().observe(getViewLifecycleOwner(), this::updateData);

        chart = binding.chart;
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

        chart.getLegend().setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextColor(Color.rgb(255, 192, 56));
        yAxis.setAxisMinimum(0f);
        yAxis.setGridColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(23);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setEnabled(true);

        return binding.getRoot();
    }

    private void updateData(List<DocumentSnapshot> documentSnapshots) {

        if (documentSnapshots.size() == 0) {
            return;
        }

        List<BarEntry> entries = documentSnapshots.stream().map(document -> document.toObject(Utility.class)).
                map(utility -> new Entry(dateToHour(utility.getTimestamp().toDate()), utility.getWaterConsumption())).
                collect(Collectors.groupingBy(Entry::getX, Collectors.summingInt(e -> (int) e.getY()))).entrySet().stream().
                map(e -> new BarEntry(e.getKey(), e.getValue())).collect(Collectors.toList());

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        BarDataSet ds = new BarDataSet(entries, "labels");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds.setDrawValues(false);
        sets.add(ds);

        chart.setData(new BarData(sets));
        chart.invalidate();
    }

    private int dateToHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
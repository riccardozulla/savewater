package it.units.sim.savewater.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.savewater.databinding.FragmentPieChartBinding;
import it.units.sim.savewater.model.Utility;

public class HalfPieChartFragment extends SharingChartFragment<PieChart> {

    private static final String TAG = "HalfPieChartActivity";
    private DashboardViewModel dashboardViewModel;
    private FragmentPieChartBinding binding;
    private int waterTarget;

    @Override
    protected View drawChart(@NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        binding = FragmentPieChartBinding.inflate(inflater, null, false);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        dashboardViewModel.getTargetWaterConsumption().observe(getViewLifecycleOwner(), this::updateTarget);
        dashboardViewModel.getDailyWaterConsumption().observe(getViewLifecycleOwner(), this::updateData);

        chart = binding.pieChart;
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setTransparentCircleAlpha(0);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);

        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        chart.setMaxAngle(240f); // HALF CHART
        chart.setRotationAngle(150f);
        chart.setCenterTextColor(Color.rgb(255, 192, 56));

        chart.animateY(1000);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);

        return binding.getRoot();
    }

    private void updateData(List<DocumentSnapshot> documentSnapshots) {
        updatePieChart(documentSnapshots.stream().map(d -> d.toObject(Utility.class).getWaterConsumption()).reduce(0, Integer::sum));
    }

    private void updateTarget(Integer integer) {
        waterTarget = integer;
    }

    private void updatePieChart(int waterConsumption) {
        ArrayList<PieEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        if (waterConsumption < waterTarget) {
            values.add(new PieEntry((float) waterConsumption, "Water used"));
            values.add(new PieEntry((float) waterTarget - waterConsumption, ""));
            colors.add(Color.GREEN);
            colors.add(Color.LTGRAY);
        } else {
            values.add(new PieEntry((float) waterConsumption, "Water used"));
            colors.add(Color.RED);
        }

        PieDataSet dataSet = new PieDataSet(values, "Water");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.setCenterText(waterConsumption + "/" + waterTarget + "\nLiters");
        chart.invalidate();
    }
}
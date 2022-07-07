package it.units.sim.savewater.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.anastr.speedviewlib.components.Section;

import java.util.Arrays;

import it.units.sim.savewater.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        dashboardViewModel =
                new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        dashboardViewModel.getInstantWaterConsumption()
                .observe(getViewLifecycleOwner(), this::updateWaterConsumption);
        dashboardViewModel.getTargetWaterConsumption()
                .observe(getViewLifecycleOwner(), this::updateTargetWaterConsumption);

        binding.speedometer.clearSections();
        binding.speedometer.addSections(
                new Section(0f, .5f, Color.LTGRAY),
                new Section(.5f, .8f, Color.YELLOW),
                new Section(.8f, 1f, Color.RED));
        binding.speedometer.setTicks(Arrays.asList(0f, .2f, .5f, .8f, 1f));
        binding.speedometer.setOnPrintTickLabel((section, number) -> {
            if (section == 3) {
                return "MAX";
            } else {
                return null;
            }
        });

        return binding.getRoot();
    }

    private void updateTargetWaterConsumption(Integer liters) {
        binding.speedometer.setMaxSpeed(liters * 1.25f);
    }

    private void updateWaterConsumption(int liters) {
        binding.speedometer.speedTo(liters, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package it.units.sim.savewater.ui.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.Chart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import it.units.sim.savewater.databinding.FragmentSharingChartBinding;

public abstract class SharingChartFragment<T extends Chart> extends Fragment {

    private static final String TAG = "SharingChartFragment";
    private FragmentSharingChartBinding binding;
    protected T chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "before super.onCreate");
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "after super.onCreate");
        binding = FragmentSharingChartBinding.inflate(inflater, container, false);
        binding.fragmentContainerView.addView(drawChart(inflater, savedInstanceState));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.buttonShare.setOnClickListener(v -> shareChart(chart));
    }

    protected abstract View drawChart(@NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState);

    private Uri saveImage(Bitmap image) {
        // Should be processed in another thread
        File imagesFolder = new File(requireActivity().getCacheDir(), "images");
        Uri uri = null;
        try {
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(requireActivity(), "it.units.sim.savewater", file); //"com.mydomain.fileprovider"

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    protected void shareChart(T chart) {
        Bitmap bitmap = chart.getChartBitmap();
        Uri bitmapUri = saveImage(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
}
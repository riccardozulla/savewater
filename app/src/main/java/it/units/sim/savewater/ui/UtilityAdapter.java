package it.units.sim.savewater.ui;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import it.units.sim.savewater.databinding.ItemUtilityBinding;
import it.units.sim.savewater.model.Utility;
import it.units.sim.savewater.utils.FirestoreAdapter;

public class UtilityAdapter extends FirestoreAdapter<UtilityAdapter.ViewHolder> {

    private final OnUtilitySelectedListener mListener;

    public UtilityAdapter(Query query, OnUtilitySelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(ItemUtilityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public DocumentSnapshot retrieveSnapshot(int position) {
        return getSnapshot(position);
    }

    public interface OnUtilitySelectedListener {
        void onUtilitySelected(DocumentSnapshot utility);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ViewHolder";
        private ItemUtilityBinding binding;

        public ViewHolder(ItemUtilityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnUtilitySelectedListener listener) {

            Utility utility = snapshot.toObject(Utility.class);
            Resources resources = itemView.getResources();
            binding.itemName.setText(utility.getName());
            binding.itemWaterConsumption.setText(String.valueOf(utility.getWaterConsumption()));
            binding.itemDescription.setText(utility.getDescription());


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onUtilitySelected(snapshot);
                    }
                }
            });
        }

    }
}
package android.mohamedalaa.com.bakingapp.view.adapters;

import android.content.Context;
import android.mohamedalaa.com.bakingapp.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohamed on 7/19/2018.
 *
 * Note always index 0 is ingredients so we make circle text view gone and it is not clickable
 * and any click we pass (position - 1);
 */
public class StepsRecyclerViewAdapter extends
        RecyclerView.Adapter<StepsRecyclerViewAdapter.StepsViewHolder> {

    private List<String> shortDescriptionList;
    private List<String> fullDescriptionList;
    private StepsAdapterListener listener;

    public StepsRecyclerViewAdapter(List<String> shortDescriptionList,
                                    List<String> fullDescriptionList,
                                    StepsAdapterListener listener) {
        this.shortDescriptionList = shortDescriptionList;
        this.fullDescriptionList = fullDescriptionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_step_list, parent, false);

        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        if (position == 0){
            holder.stepNumberTextView.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(null);
        }else {
            holder.stepNumberTextView.setVisibility(View.VISIBLE);
            holder.stepNumberTextView.setText(String.valueOf(position));

            final int stepIndex = position - 1;
            holder.itemView.setOnClickListener(view -> {
                if (listener != null){
                    listener.onItemClick(stepIndex);
                }
            });
        }

        holder.shortDescriptionTextView.setText(shortDescriptionList.get(position));

        holder.fullDescriptionTextView.setText(fullDescriptionList.get(position));
    }

    @Override
    public int getItemCount() {
        return shortDescriptionList == null ? 0 : shortDescriptionList.size();
    }

    // ----- View Holder Class

    class StepsViewHolder extends RecyclerView.ViewHolder {

        final TextView stepNumberTextView;
        final TextView shortDescriptionTextView;
        final TextView fullDescriptionTextView;

        StepsViewHolder(View itemView) {
            super(itemView);

            stepNumberTextView = itemView.findViewById(R.id.stepNumberTextView);
            shortDescriptionTextView = itemView.findViewById(R.id.shortDescriptionTextView);
            fullDescriptionTextView = itemView.findViewById(R.id.fullDescriptionTextView);
        }
    }

    // ----- Interface for this adapter events

    public interface StepsAdapterListener {

        void onItemClick(int stepIndex);

    }

}

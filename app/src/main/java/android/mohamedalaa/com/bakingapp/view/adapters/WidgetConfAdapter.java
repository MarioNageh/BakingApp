package android.mohamedalaa.com.bakingapp.view.adapters;

import android.content.Context;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.viewmodel.IngredientsWidgetConfigureViewModel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohamed on 7/23/2018.
 *
 */
public class WidgetConfAdapter extends RecyclerView.Adapter<WidgetConfAdapter.WidgetConfViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private IngredientsWidgetConfigureViewModel viewModel;
    private WidgetConfItemListener listener;

    public WidgetConfAdapter(Context context, List<Recipe> recipeList,
                             IngredientsWidgetConfigureViewModel viewModel,
                             WidgetConfItemListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WidgetConfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(
                R.layout.item_widget_configuration, parent, false);

        return new WidgetConfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetConfViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        String name = recipe.getName();
        int servings = recipe.getServings();
        String servingsString = context.getString(R.string.servings_with_variable_int, servings);
        int imageDrawableRes = recipe.getImageDrawableRes();

        holder.recipeNameTextView.setText(name);
        holder.recipeServingsTextView.setText(servingsString);
        holder.recipeImageView.setImageResource(imageDrawableRes);

        if (viewModel.indexChosen == position){
            holder.selectedFrameLayout.setVisibility(View.VISIBLE);
        }else {
            holder.selectedFrameLayout.setVisibility(View.GONE);
        }

        final int recipeIndex = position;
        holder.itemView.setOnClickListener(view -> {
            if (viewModel.indexChosen == recipeIndex){
                viewModel.indexChosen = -1;
            }else {
                viewModel.indexChosen = recipeIndex;
            }

            if (listener != null){
                listener.onItemClick();
            }

            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    // ----- View Holder Class

    class WidgetConfViewHolder extends RecyclerView.ViewHolder {

        final ImageView recipeImageView;
        final TextView recipeNameTextView;
        final TextView recipeServingsTextView;
        final FrameLayout selectedFrameLayout;

        WidgetConfViewHolder(View itemView) {
            super(itemView);

            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeServingsTextView = itemView.findViewById(R.id.recipeServingsTextView);
            selectedFrameLayout = itemView.findViewById(R.id.selectedFrameLayout);
        }
    }

    // ---- Public Methods

    public void swapList(List<Recipe> recipeList){
        this.recipeList = recipeList;

        notifyDataSetChanged();
    }

    // ----- Interface for this adapter

    public interface WidgetConfItemListener {
        void onItemClick();
    }

}

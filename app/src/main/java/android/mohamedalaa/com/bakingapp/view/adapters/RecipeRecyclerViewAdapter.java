package android.mohamedalaa.com.bakingapp.view.adapters;

import android.content.Context;
import android.mohamedalaa.com.bakingapp.R;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohamed on 7/18/2018.
 *
 */
public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {
    
    // --- Private Variables

    private final Context context;
    private final RecipesAdapterListener listener;
    private List<Recipe> recipeList;

    public RecipeRecyclerViewAdapter(Context context,
                                     RecipesAdapterListener listener, List<Recipe> recipeList) {
        this.context = context;
        this.listener = listener;
        this.recipeList = recipeList;
    }

    // ---- Overridden Methods

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutRes = R.layout.item_recipe_list;

        View view = inflater.inflate(layoutRes, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        String name = recipe.getName();
        int servings = recipe.getServings();
        String servingsString = context.getString(R.string.servings_with_variable_int, servings);
        int imageDrawableRes = recipe.getImageDrawableRes();

        holder.recipeNameTextView.setText(name);
        holder.recipeServingsTextView.setText(servingsString);
        holder.recipeImageView.setImageResource(imageDrawableRes);

        final int recipeIndex = position;
        holder.itemView.setOnClickListener(view -> {
            if (listener != null){
                listener.onItemClick(recipeIndex);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    // ----- View Holder Class

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        final TextView recipeNameTextView;
        final TextView recipeServingsTextView;
        final ImageView recipeImageView;

        RecipeViewHolder(View itemView) {
            super(itemView);

            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeServingsTextView = itemView.findViewById(R.id.recipeServingsTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
        }

    }
    
    // ---- Public Methods
    
    public void swapList(List<Recipe> recipeList){
        this.recipeList = recipeList;

        notifyDataSetChanged();
    }

    // ----- Interface for this adapter events

    public interface RecipesAdapterListener {

        void onItemClick(int recipeIndex);

    }

}

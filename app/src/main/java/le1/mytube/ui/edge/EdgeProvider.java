package le1.mytube.ui.edge;

import android.content.Context;
import android.widget.RemoteViews;

import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

import le1.mytube.R;

public class EdgeProvider extends SlookCocktailProvider {

    @Override
    public void onVisibilityChanged(Context context, int cocktailId, int visibility) {
        super.onVisibilityChanged(context, cocktailId, visibility);
    }

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager, int[] cocktailIds) {
        super.onUpdate(context, cocktailManager, cocktailIds);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.edge_player);
        if (cocktailIds != null) {
            for (int cocktailId : cocktailIds) {
                cocktailManager.updateCocktail(cocktailId, rv);
            }
        }
    }
}

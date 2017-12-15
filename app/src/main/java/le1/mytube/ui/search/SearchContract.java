package le1.mytube.ui.search;

import java.util.List;

import le1.mytube.ui.base.BaseContract;

public interface SearchContract extends BaseContract {

    interface View extends BaseContract.View {
        void onSearchResultLoaded(List<String> suggestions);
        void onSearchResultError(String message);

    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void loadAutocompleteSuggestions(String query);
    }
}

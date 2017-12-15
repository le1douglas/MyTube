package le1.mytube.presentation.ui.base;

public interface TemplateContract extends BaseContract {

    interface View extends BaseContract.View {
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);
    }
}

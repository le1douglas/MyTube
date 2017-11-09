package le1.mytube.base;

public interface BaseContract {
    interface View {
    }

    interface ViewModel<SS extends View>{
        void setContractView(SS contractView);
    }

}

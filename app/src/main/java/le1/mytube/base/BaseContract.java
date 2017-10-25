package le1.mytube.base;

public interface BaseContract {
    interface View{
        void methodInAllViews();
    }

    interface ViewModel<SS extends View>{
        void setContract(SS contract);
    }

}

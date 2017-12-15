package le1.mytube.domain.listeners;

public interface OnExecuteTaskCallback {

    void onBeforeTask();

    void onDuringTask();

    void onAfterTask(Object result);
}

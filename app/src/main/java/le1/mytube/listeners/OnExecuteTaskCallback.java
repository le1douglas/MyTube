package le1.mytube.listeners;

public interface OnExecuteTaskCallback {

    void onBeforeTask();

    void onDuringTask();

    void onAfterTask(Object result);
}

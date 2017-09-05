package le1.mytube.listeners;

/**
 * Created by Leone on 05/09/17.
 */

public interface OnExecuteTaskCallback {

    void onBeforeExecutingTask();

    void onDuringExecutingTask();

    void onAfterExecutingTask(Object result);
}

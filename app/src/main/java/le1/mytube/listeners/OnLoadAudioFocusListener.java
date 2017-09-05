package le1.mytube.listeners;

/**
 * Created by Leone on 04/09/17.
 */

public interface OnLoadAudioFocusListener {

    void onAudioFocusTrue();

    void onAudioFocusFalse();

    void onAudioFocusLoadingError();
}

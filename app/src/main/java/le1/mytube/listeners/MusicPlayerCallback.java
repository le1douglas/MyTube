package le1.mytube.listeners;

import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by leone on 08/10/17.
 */

public interface MusicPlayerCallback {

    void onUpdateSeekBar(int position);

    void onInitializeUi(YouTubeSong youTubeSong);
}

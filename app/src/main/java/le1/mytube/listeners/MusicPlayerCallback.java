package le1.mytube.listeners;

import java.util.List;

import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by leone on 08/10/17.
 */

public interface MusicPlayerCallback {

    void onUpdateSeekBar(int position);

    void onInitializeUi(List<YouTubeSong> youTubeSongs);

    void onCloseActivity();


}

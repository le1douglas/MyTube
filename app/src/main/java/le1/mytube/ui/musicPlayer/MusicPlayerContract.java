package le1.mytube.ui.musicPlayer;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.List;

import le1.mytube.base.BaseContract;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public interface MusicPlayerContract extends BaseContract {

    interface View extends BaseContract.View {
        void onInitializeUi(List<YouTubeSong> youTubeSongs);
        void onInitializeUi(YouTubeSong youTubeSongs);
        void onUpdateSeekBar(int position);
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void playOrPause();
        void seekTo(int progress);

        void linkPlayerToView(@NonNull SimpleExoPlayerView playerView);

        void startSongIfNecessary(@NonNull Intent intent, @Nullable ComponentName callingActivity);
    }
}
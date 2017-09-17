package le1.mytube.mvpModel.database.queue;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by Leone on 09/09/17.
 */
@Entity(tableName = "Queue")
public class QueueYouTubeSong {

    @PrimaryKey(autoGenerate = true)
    private int transactionNumber;

    @ColumnInfo
    private int position;

    @Embedded
    private YouTubeSong youTubeSong;

    public QueueYouTubeSong(YouTubeSong youTubeSong, int position) {
        this.youTubeSong = youTubeSong;
        this.position = position;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(int transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public void setYouTubeSong(YouTubeSong youTubeSong) {
        this.youTubeSong = youTubeSong;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public YouTubeSong getYouTubeSong() {
        return youTubeSong;
    }
}

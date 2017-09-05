package le1.mytube.mvpModel.songs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Entity(tableName = SongDatabaseConstants.TB_NAME)
public class YouTubeSong {

    @ColumnInfo(name = SongDatabaseConstants.FLD_TITLE)
    private String title;

    @PrimaryKey
    private String id;

    @ColumnInfo(name = SongDatabaseConstants.FLD_PATH)
    private String path;

    @ColumnInfo(name = SongDatabaseConstants.FLD_START)
    private Integer start;

    @ColumnInfo(name = SongDatabaseConstants.FLD_END)
    private Integer end;

    public YouTubeSong(String title, String id, String path, Integer start, Integer end) {
        this.title = title;
        this.id = id;
        this.path = path;
        if (start == null) this.start = 0;
        else this.start = start;

        if (end == null) {
            if (path != null) {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(path);
                this.end = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            } else {
                this.end = 0;
            }
        } else if (end >= start) {
            this.end = end;
        } else
            throw new IllegalArgumentException("start (" + String.valueOf(start) + ") is greater than end (" + String.valueOf(end) + ")");
    }

    public String getTitle() {
        return this.title;
    }

    public String getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public Integer getStart() {
        return this.start;
    }

    public Integer getEnd() {
        return this.end;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setEnd(Integer end) {
        //TODO add check if greater than start
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" +
                this.title + "," +
                this.id + "," +
                this.path + "," +
                this.start + "," +
                this.end + "}";
    }


    public void download(final Context context) {
        final File myTubeFolder = new File(Environment.getExternalStorageDirectory(), "MyTube");
        if (!myTubeFolder.exists()) {
            myTubeFolder.mkdirs();
        }

        final String[] downloadUrl = new String[1];
        final Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
                URL url = new URL(downloadUrl[0]);
                File f = new File(myTubeFolder, title + ".mp3");


                URLConnection connection = url.openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (lenghtOfFile > 0) // only if total length is known
                        e.onNext(total*100/ lenghtOfFile);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                e.onComplete();

            }
        });

        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 140;
                    downloadUrl[0] = ytFiles.get(itag).getUrl();

                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull Long progress) {
                                    System.out.println(progress);
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {
                                    Toast.makeText(context, "COMPLETE!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }.extract("http://youtube.com/watch?v=" + id, false, false);

    }

}

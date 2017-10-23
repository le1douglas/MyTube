package le1.mytube.mvpModel.database.song;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import at.huber.youtubeExtractor.Format;
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
import le1.mytube.mvpModel.database.DatabaseConstants;

@Entity(tableName = DatabaseConstants.TB_NAME)
public class YouTubeSong implements Parcelable{

    @PrimaryKey
    private String id;

    @ColumnInfo
    private String title;

    @ColumnInfo
    private String path;

    @ColumnInfo
    private Uri streamingUri;

    @ColumnInfo
    private Uri imageUri;

    @Ignore
    private Bitmap imageBitmap;

    //we store only one resolution to disk
    @Ignore
    private Format format;

    @ColumnInfo
    private int start;

    @ColumnInfo
    private int end;

    @ColumnInfo
    private int duration;



    public YouTubeSong(String id, String title, String path, Uri streamingUri, Uri imageUri, int start, int end, int duration) {
        this.title = title;
        this.id = id;
        this.path = path;
        this.streamingUri = streamingUri;
        this.imageUri = imageUri;
        this.start = start;
        this.end = end;
        this.duration = duration;
    }


    private YouTubeSong(String id, String title, String path, Uri streamingUri, Uri imageUri,Bitmap imageBitmap, Format format ,int start, int end, int duration) {
        this.title = title;
        this.id = id;
        this.path = path;
        this.streamingUri = streamingUri;
        this.imageUri = imageUri;
        this.imageBitmap = imageBitmap;
        this.format = format;
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected YouTubeSong(Parcel parcel) {
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.path = parcel.readString();
        if (parcel.readString()!=null) this.imageUri = Uri.parse(parcel.readString());
        else this.imageUri = null;
        this.start = parcel.readInt();
        this.end = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        if (this.path!=null) parcel.writeString(this.path);
        else parcel.writeString(null);
        if (this.imageUri !=null) parcel.writeString(this.imageUri.toString());
        else parcel.writeString(null);
        parcel.writeInt(start);
        parcel.writeInt(end);
    }

    public static final Parcelable.Creator<YouTubeSong> CREATOR = new Parcelable.Creator<YouTubeSong>() {

        public YouTubeSong createFromParcel(Parcel in) {
            return new YouTubeSong(in);
        }

        public YouTubeSong[] newArray(int size) {
            return new YouTubeSong[size];
        }
    };

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

    public Uri getImageUri() {
        return imageUri;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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
        this.end = end;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "{" +
                this.title + "," +
                this.id + "," +
                this.path + "," +
                this.imageUri + "," +
                this.start + "," +
                this.end + "," +
                this.duration + "}";
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
                        e.onNext(total * 100 / lenghtOfFile);
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

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Uri getStreamingUri() {
        return streamingUri;
    }

    public void setStreamingUri(Uri streamingUri) {
        this.streamingUri = streamingUri;
    }

    public static class Builder {
        private final String id;
        private final String title;
        private String path;
        private int start;
        private int end;
        private Uri imageUri;
        private Bitmap imageBitmap;
        private Uri streamingUri;
        private Format format;
        private int duration;

        public Builder(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder startTime(int maybemilliseconds) {
            this.start = maybemilliseconds;
            return this;
        }

        public Builder endTime(int maybemilliseconds) {
            this.end = maybemilliseconds;
            return this;
        }

        public Builder imageUri(Uri image){
            this.imageUri = image;
            return this;
        }

        public Builder streamingUri(Uri link){
            this.streamingUri = streamingUri;
            return this;
        }

        public Builder imageBitmap(Bitmap image){
            this.imageBitmap = image;
            return this;
        }

        public Builder duration(int duration){
            this.duration = duration;
            return this;
        }


        public Builder format(Format format){
            this.format = format;
            return this;
        }

        public YouTubeSong build(){
            if (this.end<this.start) throw new IllegalArgumentException("end ("+String.valueOf(this.end)+") must be grater han start("+String.valueOf(this.start)+")");
            else return new YouTubeSong(this.id, this.title, this.path, this.streamingUri,this.imageUri, this.imageBitmap, this.format,this.start, this.end, this.duration);
        }
    }
}

package le1.mytube;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

public class YouTubeSong implements Parcelable{
    private String title;
    private String id;
    private String path;
    private Integer start;
    private Integer end;

    public YouTubeSong(String title, String videoId, String path, Integer start, Integer end) throws IllegalArgumentException {
        this.title = title;
        id = videoId;
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
        } else if (end > start) {
            this.end = end;
        } else
            throw new IllegalArgumentException("start (" + String.valueOf(start) + ") is greater than end (" + String.valueOf(end) + ")");
    }


    protected YouTubeSong(Parcel in) {
        title = in.readString();
        id = in.readString();
        path = in.readString();
    }

    public static final Creator<YouTubeSong> CREATOR = new Creator<YouTubeSong>() {
        @Override
        public YouTubeSong createFromParcel(Parcel in) {
            return new YouTubeSong(in);
        }

        @Override
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(path);
    }
}

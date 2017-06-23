package le1.mytube;

import android.media.MediaMetadataRetriever;

public class YouTubeSong {
    private String mTitle;
    private String mId;
    private String mPath;
    private Integer mStart;
    private Integer mEnd;

    public YouTubeSong(String title, String videoId, String path, Integer start, Integer end) throws RuntimeException {
        mTitle = title;
        mId = videoId;
        mPath = path;
        if (start == null) mStart = 0;
        else mStart = start;

        if (end == null) {
            if (path != null) {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(path);
                mEnd = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            } else {
                mEnd = 0;
            }
        } else if (start < end) {
            mEnd = end;
        } else
            throw new RuntimeException("start (" + String.valueOf(start) + ") is greater than end (" + String.valueOf(end) + ")");
    }


    public String getTitle() {
        return this.mTitle;
    }

    public String getId() {
        return this.mId;
    }

    public String getPath() {
        return this.mPath;
    }

    public Integer getStart() {
        return this.mStart;
    }

    public Integer getEnd() {
        return this.mEnd;
    }


    public void setTitle(String title) {
        mTitle = title;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setStart(Integer start) {
        mStart = start;
    }

    public void setEnd(Integer end) {
        mEnd = end;
    }
}

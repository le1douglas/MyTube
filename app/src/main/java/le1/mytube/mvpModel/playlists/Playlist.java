package le1.mytube.mvpModel.playlists;

/**
 * Created by Leone on 25/08/17.
 */

public class Playlist {

    private String name;
    private String path;
    private int dateAdded;
    private int dayLastModified;

    public Playlist(String name, String path, int dateAdded, int dayLastModified){
        this.name = name;
        this.path = path;
        this.dateAdded = dateAdded;
        this.dayLastModified = dayLastModified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getDateAdded() {
        return dateAdded;
    }

    public int getDayLastModified() {
        return dayLastModified;
    }
}

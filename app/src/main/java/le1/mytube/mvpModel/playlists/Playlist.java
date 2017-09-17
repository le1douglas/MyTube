package le1.mytube.mvpModel.playlists;

/**
 * Created by Leone on 25/08/17.
 */

public class Playlist {

    private long id;
    private String name;
    private String path;
    private int dateAdded;
    private int dayLastModified;

    public Playlist(long id, String name, String path, int dateAdded, int dayLastModified){
        this.id = id;
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

    public long getId() {
        return id;
    }

}

package le1.mytube.mvpModel.database;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class Converters {

    @TypeConverter
    public static Uri uriFromString(String value) {
        return value == null ? null : Uri.parse(value);
    }

    @TypeConverter
    public static String stringFromUri(Uri value) {
        return value == null ? null : value.toString();
    }

}

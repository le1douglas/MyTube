package le1.mytube.mvpModel.songs;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

/**
 * Created by Leone on 05/09/17.
 */

public class Converters {


        @TypeConverter
        public static Uri uriFromString(String value) {
            return value == null ? null : Uri.parse(value);
        }

        @TypeConverter
        public static String stringToUri(Uri value) {
            return value == null ? null : value.toString();
        }

}

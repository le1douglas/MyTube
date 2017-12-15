package le1.mytube.data.sharedPref;

import android.content.Context;

/**
 * Created by leone on 25/11/17.
 */

public class SharedPrefRepoImpl implements SharedPrefRepo {

    public SharedPrefRepoImpl(Context context){

    }

    @Override
    public boolean getAudioFocus() {
        return false;
    }

    @Override
    public void setAudioFocus(boolean audioFocus) {

    }
}

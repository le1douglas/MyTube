package le1.mytube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class AutocompleteAdapter extends ArrayAdapter<String> {
    private ArrayList<String> res;
    EditText editText;

    Context context;

    public AutocompleteAdapter(Context c, ArrayList<String> r, EditText e) {
        super(c, 0, r);
        res = r;
        editText=e;
        context=c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String textToDisplay = res.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_row, parent, false);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.text);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.buttonInsertText);

        textView.setText(textToDisplay);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(textToDisplay);
                editText.setSelection(editText.length());
            }
        });

        return convertView;
    }
}

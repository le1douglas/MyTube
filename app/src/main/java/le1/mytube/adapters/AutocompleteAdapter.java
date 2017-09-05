package le1.mytube.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import le1.mytube.R;


public class AutocompleteAdapter extends ArrayAdapter<String> {
    EditText editText;
    Context context;
    private ArrayList<String> arrayList;

    public AutocompleteAdapter(Context context, ArrayList<String> arrayList, EditText editText) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.editText = editText;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final String textToDisplay = arrayList.get(position);

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

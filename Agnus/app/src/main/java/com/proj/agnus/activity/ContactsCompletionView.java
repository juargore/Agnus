package com.proj.agnus.activity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Person;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Arturo on 3/9/2018.
 */

public class ContactsCompletionView extends TokenCompleteTextView<Person> {

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Person person) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = l.inflate(R.layout.contact_token, (ViewGroup)findViewById(R.id.lay));

        TextView name = (TextView) layout.findViewById(R.id.name);
        name.setText(person.getName());

        return name;
    }

    @Override
    protected Person defaultObject(String completionText) {
        return null;
    }
}
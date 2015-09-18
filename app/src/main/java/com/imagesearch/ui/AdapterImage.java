package com.imagesearch.ui;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagesearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sabelo on 9/16/15.
 */
public class AdapterImage extends ArrayAdapter<ModelImage> {

    public AdapterImage(Context context, List<ModelImage> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null) {
            convertView = LayoutInflater.from( getContext() ).inflate(R.layout.adapter_image, parent, false);
        }
        ModelImage image = getItem(position);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        ivImage.setImageResource(0);
        Picasso.with(getContext()).load(image.tbUrl).into(ivImage);
        tvTitle.setText(Html.fromHtml( image.title) );

        return convertView;
    }

}
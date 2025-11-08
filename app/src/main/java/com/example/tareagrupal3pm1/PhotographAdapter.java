package com.example.tareagrupal3pm1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PhotographAdapter extends ArrayAdapter<Photograph> {

    private Context mContext;
    private int mResource;

    public PhotographAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Photograph> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        byte[] imagenBytes = getItem(position).getImagen();
        String descripcion = getItem(position).getDescripcion();

        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.imgFoto = convertView.findViewById(R.id.itemImgFoto);
            holder.txtDescripcion = convertView.findViewById(R.id.itemTxtDescripcion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgFoto.setImageBitmap(bitmap);
        holder.txtDescripcion.setText(descripcion);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgFoto;
        TextView txtDescripcion;
    }
}

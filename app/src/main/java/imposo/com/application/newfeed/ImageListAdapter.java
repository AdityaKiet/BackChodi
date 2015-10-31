package imposo.com.application.newfeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import imposo.com.application.R;

/**
 * Created by adityaagrawal on 01/11/15.
 */
public class ImageListAdapter extends BaseAdapter {
    private Context context;

    public ImageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return AddNewFeed.imageNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        rowView = mInflater.inflate(R.layout.single_row_images, null);

        TextView txtOption = ((TextView) rowView.findViewById(R.id.txtOption));
        txtOption.setText(AddNewFeed.imageNames.get(position));
        ((ImageButton)rowView.findViewById(R.id.btnDeleteOption)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFeed.imageNames.remove(position);
                AddNewFeed.bitmaps.remove(position);
                notifyDataSetChanged();
            }
        });

        ImageView imageView = ((ImageView)rowView.findViewById(R.id.imgSelectedImage));
        imageView.setImageBitmap(AddNewFeed.bitmaps.get(position));
        return rowView;
    }
}

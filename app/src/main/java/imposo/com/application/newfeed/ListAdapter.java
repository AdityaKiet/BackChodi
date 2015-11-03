package imposo.com.application.newfeed;

/**
 * Created by adityaagrawal on 09/10/15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import imposo.com.application.R;
import imposo.com.application.dto.PhoneContactsDTO;
import imposo.com.application.ui.textdrawable.CircleTextDrawable;
import imposo.com.application.ui.textdrawable.ColorGenerator;

public class ListAdapter extends BaseAdapter {
    private List<PhoneContactsDTO> PhoneContactsDTOs;
    private Context context;
    private int[] colors = {
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff9575cd,
            0xff7986cb,
            0xff64b5f6,
            0xff4fc3f7,
            0xff4dd0e1,
            0xff4db6ac,
            0xff81c784,
            0xffaed581,
            0xffff8a65,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xffa1887f,
            0xff90a4ae
    };
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private CircleTextDrawable.IBuilder mDrawableBuilder;

    public ListAdapter(Context context, List<PhoneContactsDTO> PhoneContactsDTOs) {
        this.context = context;
        this.PhoneContactsDTOs = PhoneContactsDTOs;
        mDrawableBuilder = CircleTextDrawable.builder().round();
    }

    @Override
    public int getCount() {
        return PhoneContactsDTOs.size();
    }

    @Override
    public Object getItem(int position) {
        return PhoneContactsDTOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.contact_single_row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.name.setText(PhoneContactsDTOs.get(position).getName().trim());
        holder.phoneNumber.setText(PhoneContactsDTOs.get(position).getPhoneNumber());
        int rand  = PhoneContactsDTOs.get(position).getName().length() % colors.length;
        if(PhoneContactsDTOs.get(position).getName().length() == 0){
            holder.imageView.setImageDrawable(mDrawableBuilder.build(String.valueOf('N'), mColorGenerator.getColor(colors[rand])));
        }
        else{
            holder.imageView.setImageDrawable(mDrawableBuilder.build(String.valueOf(PhoneContactsDTOs.get(position).getName().toUpperCase().charAt(0)), mColorGenerator.getColor(colors[rand])));
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView phoneNumber;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imgContact);
            name = (TextView) view.findViewById(R.id.txtName);
            phoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
        }
    }
}


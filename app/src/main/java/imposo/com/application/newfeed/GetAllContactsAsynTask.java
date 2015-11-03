package imposo.com.application.newfeed;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import imposo.com.application.dto.PhoneContactsDTO;
import imposo.com.application.global.GlobalData;

/**
 * Created by adityaagrawal on 09/10/15.
 */
public class GetAllContactsAsynTask extends AsyncTask<Void, Void, Void>{
    private Context context;
    private ListView nestedListView;
    private List<PhoneContactsDTO> PhoneContactsDTOs = new ArrayList<>();

    public GetAllContactsAsynTask(Context context, ListView view){
        this.context = context;
        this.nestedListView = view;

    }


    @Override
    protected Void doInBackground(Void... params) {
        String[] arrayColumns = new String[]{ ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        PhoneContactsDTO PhoneContactsDTO = null;
        while (cursor.moveToNext())
        {
            PhoneContactsDTO =  new PhoneContactsDTO();
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            PhoneContactsDTO.setName(name);
            phoneNumber = phoneNumber.replaceAll(" ", "");
            phoneNumber = phoneNumber.replaceAll("-", "");
            PhoneContactsDTO.setPhoneNumber(phoneNumber);
            PhoneContactsDTOs.add(PhoneContactsDTO);
            PhoneContactsDTO = null;
        }

        Set<PhoneContactsDTO> hs = new LinkedHashSet<>();
        hs.addAll(PhoneContactsDTOs);
        PhoneContactsDTOs.clear();
        PhoneContactsDTOs.addAll(hs);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Collections.sort(PhoneContactsDTOs, new NameComparator());
        ((GlobalData)context.getApplicationContext()).setContactsDTOList(PhoneContactsDTOs);
        ListAdapter adapter = new ListAdapter(context, PhoneContactsDTOs);
        nestedListView.setAdapter(adapter);
    }

    class NameComparator implements Comparator<PhoneContactsDTO> {
        @Override
        public int compare(PhoneContactsDTO a, PhoneContactsDTO b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }
}

package imposo.com.application.ui;

import android.support.v7.app.ActionBarActivity;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;

import imposo.com.application.dto.MessageCustomDialogDTO;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class MessageDialog {
    private MessageCustomDialogDTO messageCustomDialogDTO;

    public MessageDialog(MessageCustomDialogDTO messageCustomDialogDTO) {
        this.messageCustomDialogDTO = messageCustomDialogDTO;
    }

    public void show() {
        SimpleDialogFragment.createBuilder(
                messageCustomDialogDTO.getContext(), ((ActionBarActivity) messageCustomDialogDTO.getContext()).getSupportFragmentManager()).
                setTitle(messageCustomDialogDTO.getTitle())
                .setMessage(messageCustomDialogDTO.getMessage()).
                setPositiveButtonText(messageCustomDialogDTO.getButton())
                .show();
    }
}

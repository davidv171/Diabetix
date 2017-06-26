package david.projectclouds;

import android.app.Activity;
import android.content.Context;

import com.microsoft.onedrivesdk.picker.IPicker;
import com.microsoft.onedrivesdk.picker.LinkType;
import com.microsoft.onedrivesdk.picker.Picker;

/**
 * Created by david on 29.5.2017.
 */

public class OneDriveDownload {
    private IPicker mPicker;
    private String ONEDRIVE_APP_ID = "afbdf902-f875-4c83-b37f-bc36e1db48de";
    public void startFilePicker(Context context){
        mPicker = Picker.createPicker(ONEDRIVE_APP_ID);
       // mPicker.startPicking((Activity)context, LinkType.WebViewLink);
        mPicker.startPicking((Activity)context,LinkType.WebViewLink);

    }
    public IPicker getFilePicker(){
        if(mPicker!=null) {
            return this.mPicker;
        }
        else{
            System.out.println("FILE PICKER IS NULL!!!");
            return null;
        }
    }
    //TODO: GET RESULT FROM FILE PICKER

}

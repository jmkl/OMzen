package hello.dcsms.omzen.Traffic;

import android.content.Context;
import android.content.Intent;

public interface BroadcastSubReceiver {
    void onBroadcastReceived(Context context, Intent intent);
}

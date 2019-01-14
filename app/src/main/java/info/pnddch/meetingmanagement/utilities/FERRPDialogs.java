package info.pnddch.meetingmanagement.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import info.pnddch.meetingmanagement.R;


public class FERRPDialogs {
    Context context;

    static class C03421 implements OnClickListener {
        C03421() {
        }

        public void onClick(View v) {
        }
    }

    static class C03432 implements OnClickListener {
        C03432() {
        }

        public void onClick(View v) {
        }
    }

    static class C03443 implements OnClickListener {
        C03443() {
        }

        public void onClick(View v) {
        }
    }

    static class C03454 implements OnClickListener {
        C03454() {
        }

        public void onClick(View v) {
        }
    }

    public static void showFERRPErrorAlert(Activity activity, String errrorTitle, String errorMessage) {
        try {
            Snackbar bar = Snackbar.make(activity.getCurrentFocus(), (CharSequence) errorMessage, -2);
            bar.setAction((CharSequence) "DISMISS", new C03421());
            bar.setActionTextColor(-16776961);
            ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(SupportMenu.CATEGORY_MASK);
            bar.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showFERRPWarningAlert(Activity activity, String warningTitle, String warningMessage) {
        try {
            Snackbar bar = Snackbar.make(activity.getCurrentFocus(), (CharSequence) warningMessage, -2);
            bar.setAction((CharSequence) "DISMISS", new C03432());
            bar.setActionTextColor(-16776961);
            ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(InputDeviceCompat.SOURCE_ANY);
            bar.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showFERRPInfoAlert(Activity activity, String infoTitle, String infoMessage) {
        try {
            Snackbar bar = Snackbar.make(activity.getCurrentFocus(), (CharSequence) infoMessage, -2);
            bar.setAction((CharSequence) "DISMISS", new C03443());
            ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(-16711681);
            bar.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showFERRPSuccessAlert(Activity activity, String successTitle, String successMessage) {
        try {
            Snackbar bar = Snackbar.make(activity.getCurrentFocus(), (CharSequence) successMessage, -2);
            bar.setAction((CharSequence) "DISMISS", new C03454());
            bar.setActionTextColor(-16776961);
            ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(-16711936);
            bar.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

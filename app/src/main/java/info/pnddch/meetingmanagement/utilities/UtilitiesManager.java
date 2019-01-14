package info.pnddch.meetingmanagement.utilities;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilitiesManager {
    public static String imageDirPath;
    Activity activity;
    Context context;

    public UtilitiesManager(Context c, Activity act) {
        context = c;
        activity = act;
    }

    public void createMMDirectories() {
        try {
            String dirPath = context.getExternalFilesDir(null).getAbsolutePath();
            String parentDirectory = dirPath + "/mm/";
            String dBDirectory = dirPath + "/mm/db";
            String imageDirPath = dirPath + "/mm/img";
            File flParentDirectory = createDirectoryWithPermission(parentDirectory);
            File flDBDirectory = createDirectoryWithPermission(dBDirectory);
            File flImagesDirectory = createDirectoryWithPermission(imageDirPath);
            String strdbfile = "mm.db";
            File dbFile = new File(dBDirectory + "/" + strdbfile);
            if (!dbFile.exists()) {
                copyDatabaseToDirectory(strdbfile, dbFile);
            }
        } catch (Exception e) {
            FERRPDialogs.showFERRPErrorAlert(activity, "error in creation directory structure", e.getLocalizedMessage());
        }
    }

    public File createDirectoryWithPermission(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir.setReadable(true, false);
        dir.setWritable(true, false);
        return dir;
    }

    private void copyDatabaseToDirectory(String dbFile, File dbStorageFile) {
        try {
            boolean isFileCreated = dbStorageFile.createNewFile();
            InputStream myInput = context.getAssets().open(dbFile);
            OutputStream myOutput = new FileOutputStream(dbStorageFile);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = myInput.read(buffer);
                if (length > 0) {
                    myOutput.write(buffer, 0, length);
                } else {
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();
                    return;
                }
            }
        } catch (Exception e) {
            FERRPDialogs.showFERRPErrorAlert(activity, "error in copying database", e.getLocalizedMessage());
        }
    }

    public String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}

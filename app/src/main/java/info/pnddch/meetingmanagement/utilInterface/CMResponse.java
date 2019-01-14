package info.pnddch.meetingmanagement.utilInterface;

import org.json.JSONException;

public interface CMResponse {
    void consumeResponse(String str, boolean z) throws JSONException;
}

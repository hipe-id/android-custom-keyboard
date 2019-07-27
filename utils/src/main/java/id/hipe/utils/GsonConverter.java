package id.hipe.utils;

import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by Dika Putra on 09/07/18.
 */
public class GsonConverter {
    public static JSONObject bundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}

package claudetsiang.fr.model;

import org.json.JSONArray;

public class TimeModel {
    String id_date, id_time, time;

    public TimeModel(String id_date, String id_time, String time) {
        this.id_date = id_date;
        this.id_time = id_time;
        this.time = time;
    }

    public String getId_date() {
        return id_date;
    }

    public String getId_time() {
        return id_time;
    }

    public String getTime() {
        return time;
    }
}

package claudetsiang.fr.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class PlanningModel {
    int id_date;
    String date;
    JSONArray asso_date_times;

    public PlanningModel(int id_date, String date, JSONArray asso_date_times) {
        this.id_date = id_date;
        this.date = date;
        this.asso_date_times = asso_date_times;
    }

    public int getId_date() {
        return id_date;
    }

    public String getDate() {
        return date;
    }

    public JSONArray getAsso_date_times() {
        return asso_date_times;
    }
}

package claudetsiang.fr.model;

public class AppointmentItemModel {
    int id_appointment;
    String date, time, avatar, username;

    public AppointmentItemModel(int id_appointment, String date, String time, String avatar, String username) {
        this.id_appointment = id_appointment;
        this.date = date;
        this.time = time;
        this.avatar = avatar;
        this.username = username;
    }

    public int getId_appointment() {
        return id_appointment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }
}

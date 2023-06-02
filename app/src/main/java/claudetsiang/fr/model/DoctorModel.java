package claudetsiang.fr.model;


public class DoctorModel {
    String id_actor, name,email, avatar, address, tel;

    public DoctorModel(String id_actor, String name, String email, String avatar, String address, String tel) {
        this.id_actor = id_actor;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.address = address;
        this.tel = tel;
    }

    public String getId_actor() {
        return id_actor;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAddress() {
        return address;
    }

    public String getTel() {
        return tel;
    }
}

package olx.tcardoso.olxapp.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import olx.tcardoso.olxapp.helper.ConfigurationFirebase;

public class Advertisement {

    private String idAdvertisement;
    private String state;
    private String category;
    private String title;
    private String value;
    private String phone;
    private String description;
    private List<String> photo;

    public Advertisement() {
        DatabaseReference advertisementRef = ConfigurationFirebase.getFirebase()
                .child("meus_anuncios");
        setIdAdvertisement(advertisementRef.push().getKey());
    }

    public void save(){
        String idUser = ConfigurationFirebase.getIdUser();
        DatabaseReference advertisementRef = ConfigurationFirebase.getFirebase()
                .child("meus_anuncios");

        advertisementRef.child(idUser)
                .child(getIdAdvertisement())
                .setValue(this);

        saveAdvertisementPublic();
    }

    public void remove(){
        String idUser = ConfigurationFirebase.getIdUser();
        DatabaseReference advertisementRef = ConfigurationFirebase.getFirebase()
                .child("meus_anuncios")
                .child(idUser)
                .child(getIdAdvertisement());

        advertisementRef.removeValue();
        removeAdvertisementPublic();
    }

    public void removeAdvertisementPublic(){
        DatabaseReference advertisementRef = ConfigurationFirebase.getFirebase()
                .child("anuncios")
                .child(getState())
                .child(getCategory())
                .child(getIdAdvertisement());

        advertisementRef.removeValue();
    }


    public void saveAdvertisementPublic(){
        DatabaseReference advertisementRef = ConfigurationFirebase.getFirebase()
                .child("anuncios");

        advertisementRef.child(getState())
                .child(getCategory())
                .child(getIdAdvertisement())
                .setValue(this);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdAdvertisement() {
        return idAdvertisement;
    }

    public void setIdAdvertisement(String idAdvertisement) {
        this.idAdvertisement = idAdvertisement;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }
}

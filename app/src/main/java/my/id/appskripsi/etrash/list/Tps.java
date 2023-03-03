package my.id.appskripsi.etrash.list;

import java.io.Serializable;

public class Tps implements Serializable {
    public String idtps;
    public String nmtps;
    public String alamat;
    public String idkecamatan;
    public String nmkecamatan;
    public String waktujemput;
    public String deskripsi;
    public String latitude;
    public String longitude;
    public String jarak;
    public String waktutempuh;

    public Tps(String idtps, String nmtps, String alamat, String idkecamatan, String nmkecamatan, String waktujemput, String deskripsi, String latitude, String longitude, String jarak, String waktutempuh) {
        this.idtps = idtps;
        this.nmtps = nmtps;
        this.alamat = alamat;
        this.idkecamatan = idkecamatan;
        this.nmkecamatan = nmkecamatan;
        this.waktujemput = waktujemput;
        this.deskripsi = deskripsi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.jarak = jarak;
        this.waktutempuh = waktutempuh;
    }

}
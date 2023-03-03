package my.id.appskripsi.etrash.list;

import java.io.Serializable;

public class Kecamatan implements Serializable {
    public String idkecamatan;
    public String nmkecamatan;

    public Kecamatan(String idkecamatan, String nmkecamatan) {
        this.idkecamatan = idkecamatan;
        this.nmkecamatan = nmkecamatan;
    }

}
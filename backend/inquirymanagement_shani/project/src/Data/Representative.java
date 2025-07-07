package Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Representative implements Serializable {
    private int code;
    private int id;
    private String name;
    private static int representativeNextCodeVal = 0;

    public static int getRepresentativeNextCodeVal() {
        return representativeNextCodeVal;
    }

    public static void setRepresentativeNextCodeVal(int representativeNextCodeVal) {
        Representative.representativeNextCodeVal = representativeNextCodeVal;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Representative(Integer id, String name) {
        code=representativeNextCodeVal++;
        this.name = name;
        this.id = id;
    }

    public Representative() {
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return "Representative{" +
                "code=" + code +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

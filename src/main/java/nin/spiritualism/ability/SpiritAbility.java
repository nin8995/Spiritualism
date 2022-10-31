package nin.spiritualism.ability;

import java.util.ArrayList;
import java.util.List;

public enum SpiritAbility {
    TEST("test"),
    SPECTRAL_REFLECTION("spectral_reflection");

    private String id;
    public static List<String> sas = new ArrayList<>();

    static {
        for (SpiritAbility value : values()) {
            sas.add(value.id);
        }
    }

    SpiritAbility(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

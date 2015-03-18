package pojo;

import java.io.Serializable;

public enum FlagsEnum implements Serializable {

    GET_DATA(1),
    LOG_IN(2),
    LOG_OUT(3),
    REG_USER(4);

    private int value;

    FlagsEnum(int i) {
        this.value = i;
    }
}

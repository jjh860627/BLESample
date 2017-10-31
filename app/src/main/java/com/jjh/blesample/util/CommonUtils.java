package com.jjh.blesample.util;

import java.util.UUID;

/**
 * Created by jjh860627 on 2017. 10. 27..
 */

public class CommonUtils {
    public static final int getUUIDHead(UUID uuid){
        return ((int)(uuid.getMostSignificantBits() >> 32)) & 0x0000FFFF;
    }
}

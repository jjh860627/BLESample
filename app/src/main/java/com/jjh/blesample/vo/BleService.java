package com.jjh.blesample.vo;

import java.util.UUID;

/**
 * Created by jjh860627 on 2017. 10. 27..
 */

public class BleService {
    private UUID uuid;
    private String name;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package ru.clevertec.cache.util;

import lombok.Data;

@Data
public class Properties {

    private PostgresProperties postgres;
    private String cacheType;
    private Integer cacheMaxSize;

}

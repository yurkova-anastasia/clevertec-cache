package ru.clevertec.spring_core.util.yml;

import lombok.Data;

@Data
public class Properties {

    private PostgresProperties postgres;
    private CacheProperties cache;
}
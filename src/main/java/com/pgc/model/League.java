package com.pgc.model;

public record League (
        long id,
        String name,
        String ticket,
        String banner,
        String tier
) {
}

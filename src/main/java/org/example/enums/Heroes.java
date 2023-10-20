package org.example.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Heroes {

    GUTS("Guts"), KANEKI("Kaneki"), NARUTO("Naruto"), MITSURI("Mitsuri");
    private final String heroes;

    @Override
    public String toString() {
        return "Heroes{" +
                "heroes='" + heroes + '\'' +
                '}';
    }
}

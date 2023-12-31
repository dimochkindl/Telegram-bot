package org.example.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(431), EUR(451), RUB(456), BYN(0);
    private final int id;

    @Override
    public String toString() {
        return super.toString();
    }
}

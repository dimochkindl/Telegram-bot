package org.example.service;

import org.example.entities.Currency;
import org.example.service.impl.NbrbCurrencyConversionService;

public interface CurrencyConversionService {
    static CurrencyConversionService getInstance(){
        return new NbrbCurrencyConversionService();
    }

    double getConversionRatio(Currency original, Currency target);
}

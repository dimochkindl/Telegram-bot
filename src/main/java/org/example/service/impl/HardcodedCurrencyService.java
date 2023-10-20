package org.example.service.impl;

import org.example.entities.Currency;
import org.example.service.CurrencyConversionService;

public class HardcodedCurrencyService implements CurrencyConversionService {
    @Override
    public double getConversionRatio(Currency from, Currency to) {
        switch (from){
            case BYN :
                switch (to){
                    case USD :
                        return 1/2.5;
                    case EUR:
                        return 1/3.1;
                    case RUB:
                        return 32.35;
                    case BYN:
                        return 1;
                }
            case EUR:
                switch (to){
                    case USD :
                        return 1.1;
                    case BYN:
                        return 3.1;
                    case EUR:
                        return 1;
                    case RUB:
                        return 89.17;
                }
            case USD:
                switch (to){
                    case USD :
                        return 1;
                    case BYN:
                        return 2.5;
                    case EUR:
                        return 1/1.1;
                    case RUB:
                        return 81.1;
                }
            case RUB:
                switch (to){
                    case USD :
                        return 1/81.1;
                    case BYN:
                        return 1/32.35;
                    case EUR:
                        return 1/89.17;
                    case RUB:
                        return 1;
                }
        }
        return 0;
    }
}

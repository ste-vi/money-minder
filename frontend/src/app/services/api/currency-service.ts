import { Injectable } from '@angular/core';
import { Currency } from '../../models/currency';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CurrencyService {
  getCurrencies(): Observable<Currency[]> {
    const hryvniaCurrency: Currency = {
      id: 1,
      name: 'Hryvnia',
      shortName: 'UAH',
      sign: '₴',
    };
    const dollarCurrency: Currency = {
      id: 2,
      name: 'Dollar',
      shortName: 'USD',
      sign: '$',
    };
    const euroCurrency: Currency = {
      id: 3,
      name: 'Euro',
      shortName: 'EUR',
      sign: '€',
    };

    return of([hryvniaCurrency, dollarCurrency, euroCurrency]);
  }
}

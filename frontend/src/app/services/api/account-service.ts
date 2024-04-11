import { Injectable } from '@angular/core';
import { Currency } from '../../models/currency';
import { Observable, of } from 'rxjs';
import { Type } from '../../models/type';
import { Account } from '../../models/account';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  protected types: Type[] = [];

  getTypes(): Observable<Type[]> {
    const dollarCurrency: Currency = {
      id: 1,
      name: 'Dollar',
      shortName: 'USD',
      sign: '$',
    };
    const hryvniaCurrency: Currency = {
      id: 2,
      name: 'Hryvnia',
      shortName: 'UAH',
      sign: '₴',
    };

    this.types = [
      {
        id: 1,
        name: 'Bank accounts',
        accounts: [
          { id: 1, name: 'Mono', balance: 10000, currency: hryvniaCurrency },
          { id: 2, name: 'Privat', balance: 5000, currency: hryvniaCurrency },
        ],
        defaultCurrency: hryvniaCurrency,
      },
      {
        id: 2,
        name: 'Cash',
        accounts: [
          { id: 1, name: 'Wallet', balance: 1000, currency: hryvniaCurrency },
          { id: 2, name: 'Dollar', balance: 50000, currency: dollarCurrency },
        ],
        defaultCurrency: hryvniaCurrency,
      },
      {
        id: 3,
        name: 'Stocks & Crypto',
        accounts: [
          { id: 1, name: 'Wallet', balance: 1000, currency: dollarCurrency },
        ],
        defaultCurrency: hryvniaCurrency,
      },
    ];
    return of(this.types);
  }

  createAccount(account: Account, type: Type): void {
    let typeFound = this.types.find((c) => c.id === type.id);
    if (!typeFound) {
      typeFound = { ...type, accounts: [] };
      typeFound.defaultCurrency = account.currency;
      this.types.push(typeFound);
    }
    typeFound.accounts.push(account);
  }
}

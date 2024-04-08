import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
import { Transaction } from '../../models/transaction';
import { Account } from '../../models/account';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  protected transactions: Transaction[] = [];

  private account: Account = {
    id: 1,
    name: 'Mono Black',
    balance: 0,
    currency: {
      id: 1,
      name: 'Hryvnia',
      shortName: 'UAH',
      sign: '₴',
    },
  };

  getLastTransactions(): Observable<Transaction[]> {
    const lastTransactions: Transaction[] = [
      {
        id: 1,
        name: 'Food',
        amount: 100.15,
        date: new Date(),
        account: this.account,
      },
      {
        id: 2,
        name: 'Taxi',
        amount: 50.1,
        date: new Date(),
        account: this.account,
      },
      {
        id: 3,
        name: 'Delivery',
        amount: 20.457,
        date: new Date(),
        account: this.account,
      },
      {
        id: 4,
        name: 'Electronics',
        amount: 3000,
        date: new Date(),
        account: this.account,
      },
      {
        id: 5,
        name: 'Lifecell',
        amount: 300,
        date: new Date(),
        account: this.account,
      },
    ];

    return of(lastTransactions);
  }
}

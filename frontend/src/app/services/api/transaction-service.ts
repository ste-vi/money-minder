import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Transaction } from '../../models/transaction';
import { Account } from '../../models/account';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private account: Account = {
    id: "1",
    name: 'Mono Black',
    balance: 0,
    currency: {
      code: 1,
      fullName: 'Hryvnia',
      shortName: 'UAH',
      sign: '₴',
    },
    type: {id: 1, fullName: "Bank account", iconName: "bank-account"},
  };

  protected transactions: Transaction[] = [
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

  getLastTransactions(): Observable<Transaction[]> {
    return of(this.transactions);
  }
}

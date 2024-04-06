import { Component } from '@angular/core';
import { NavComponent } from '../nav/nav.component';
import { MatIcon } from '@angular/material/icon';
import { Category } from '../../models/category';
import { Currency } from '../../models/currency';
import { NgForOf } from '@angular/common';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [NavComponent, MatIcon, NgForOf],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  protected categories: Category[] = [];

  constructor() {
    this.initDummyData();
  }

  private initDummyData() {
    const dollarCurrency: Currency = { id: 1, name: 'Dollar', sign: '$' };
    const hryvniaCurrency: Currency = { id: 2, name: 'Hryvnia', sign: '₴' };

    this.categories = [
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
        id: 1,
        name: 'Cash',
        accounts: [
          { id: 1, name: 'Wallet', balance: 1000, currency: hryvniaCurrency },
          { id: 2, name: 'Dollar', balance: 50000, currency: dollarCurrency },
        ],
        defaultCurrency: hryvniaCurrency,
      },
      {
        id: 1,
        name: 'Stocks & Crypto',
        accounts: [
          { id: 1, name: 'Wallet', balance: 1000, currency: dollarCurrency },
        ],
        defaultCurrency: hryvniaCurrency,
      },
    ];
  }

  calculateCategoryTotal(category: Category): string {
    let total = 0;
    for (const account of category.accounts) {
      total += account.balance;
    }
    return total.toFixed(2);
  }
}

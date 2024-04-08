import { Component } from '@angular/core';
import { NavComponent } from '../common/nav/nav.component';
import { MatIcon } from '@angular/material/icon';
import { Type } from '../../models/type';
import { Currency } from '../../models/currency';
import { NgForOf } from '@angular/common';
import { AccountService } from '../../services/api/account-service';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [NavComponent, MatIcon, NgForOf],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  protected categories: Type[] = [];

  constructor(private accountService: AccountService) {
    this.accountService.getCategories().subscribe((categories) => {
      this.categories = categories;
    });
  }

  calculateTypeTotal(type: Type): string {
    let total = 0;
    for (const account of type.accounts) {
      total += account.balance;
    }
    return total.toFixed(2);
  }
}

import { Component } from '@angular/core';
import { NavComponent } from '../nav/nav.component';
import { MatIcon } from '@angular/material/icon';
import { Category } from '../../models/category';
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
  protected categories: Category[] = [];

  constructor(private accountService: AccountService) {
    this.accountService.getCategories().subscribe((categories) => {
      this.categories = categories;
    });
  }

  calculateCategoryTotal(category: Category): string {
    let total = 0;
    for (const account of category.accounts) {
      total += account.balance;
    }
    return total.toFixed(2);
  }
}

import {Component} from '@angular/core';
import {NavComponent} from '../common/nav/nav.component';
import {MatIcon} from '@angular/material/icon';
import {Type} from '../../models/type';
import {NgForOf} from '@angular/common';
import {AccountService} from '../../services/api/account-service';
import {ViewAccountService} from "../../services/communication/view-account-service";
import {Account} from "../../models/account";

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [NavComponent, MatIcon, NgForOf],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  protected types: Type[] = [];

  constructor(private accountService: AccountService, private viewAccountService: ViewAccountService) {
    this.loadAccounts();
    this.accountService.newAccount$.subscribe(() => {
      this.loadAccounts();
    });
  }

  private loadAccounts() {
    this.accountService.getAccounts().subscribe((accounts) => {
      this.types = accounts.reduce((acc: Type[], account) => {
        const type = acc.find((t) => t.id === account.type.id);
        if (type) {
          type.accounts.push(account);
        } else {
          acc.push({
            id: account.type.id,
            name: account.type.fullName,
            accounts: [account],
            defaultCurrency: account.currency,
          });
        }
        return acc;
      }, []);
    });
  }

  calculateTypeTotal(type: Type): string {
    let total = 0;
    for (const account of type.accounts) {
      total += account.balance;
    }
    return total.toFixed(2);
  }

  openAccountView(account: Account) {
    this.viewAccountService.openModal(account);
  }
}

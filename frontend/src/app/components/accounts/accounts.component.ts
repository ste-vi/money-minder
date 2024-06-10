import {Component} from '@angular/core';
import {NavComponent} from '../common/nav/nav.component';
import {MatIcon} from '@angular/material/icon';
import {TypeGroupedAccounts} from '../../models/typeGroupedAccounts';
import {NgForOf} from '@angular/common';
import {AccountService} from '../../services/api/account-service';
import {ViewAccountService} from "../../services/communication/view-account-service";
import {Account} from "../../models/account";
import {NetWorthWidgetComponent} from "../common/widgets/net-worth-widget/net-worth-widget.component";

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [NavComponent, MatIcon, NgForOf, NetWorthWidgetComponent],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  protected types: TypeGroupedAccounts[] = [];

  constructor(private accountService: AccountService,
              private viewAccountService: ViewAccountService) {
    this.loadAccounts();
    this.accountService.newAccount$.subscribe(() => {
      this.loadAccounts();
    });
  }

  private loadAccounts() {
    this.accountService.getTypeGroupedAccounts().subscribe(types => this.types = types)
  }

  openAccountView(account: Account) {
    this.viewAccountService.openModal(account);
  }
}

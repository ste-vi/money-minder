import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Account} from "../../../../../../models/account";
import {AccountService} from "../../../../../../services/api/account-service";
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-transaction-account-filter',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    MatIcon
  ],
  templateUrl: './transaction-account-filter.component.html',
  styleUrl: './transaction-account-filter.component.scss'
})
export class TransactionAccountFilterComponent {

  @Input() selectedAccount: Account | undefined = undefined;
  @Output() accountSelected = new EventEmitter<Account>();
  @Output() closed = new EventEmitter<void>();

  protected accounts: Account[] = [];

  constructor(private accountService: AccountService) {
    this.accountService.getAccounts().subscribe(a => this.accounts = a)
  }

  selectAccount(account: Account) {
    this.selectedAccount = account;
  }

  reset() {
    this.accountSelected.emit(undefined);
  }

  closeModal() {
    this.closed.emit();
  }

  apply() {
    this.accountSelected.emit(this.selectedAccount);
  }
}

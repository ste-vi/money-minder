import {Component} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {CategorySettingsService} from '../../services/communication/category-settings-service';
import {AccountService} from "../../services/api/account-service";
import {Account} from "../../models/account";
import {NgIf} from "@angular/common";
import {
  TransactionAccountFilterComponent
} from "../common/transaction/search-transactions/filters/transaction-account-filter/transaction-account-filter.component";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatIcon, NgIf, TransactionAccountFilterComponent],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent {

  protected defaultAccount: Account | undefined = undefined;
  protected isAccountFilterOpened: boolean = false;

  constructor(private categorySettingsService: CategorySettingsService,
              private accountService: AccountService) {
    this.accountService.getDefaultAccount().subscribe(account => this.defaultAccount = account);
  }

  openCategorySettings() {
    this.categorySettingsService.openModal(true);
  }

  selectDefaultAccount() {
    this.isAccountFilterOpened = true;
  }

  closeAccountFilter() {
    this.isAccountFilterOpened = false;
  }

  onAccountSelected(account: Account) {
    console.log(account);
    this.defaultAccount = account;
    this.accountService.updateDefaultAccount(account).subscribe()
  }
}

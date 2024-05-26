import {Component} from '@angular/core';
import {SelectAccountTypeServiceService} from '../../../services/communication/select-account-type-service.service';
import {NgForOf, NgIf} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {AccountType} from '../../../models/account-type';
import {CreateAccountService} from '../../../services/communication/create-account-service';
import {AccountService} from "../../../services/api/account-service";

@Component({
  selector: 'app-select-account-type',
  standalone: true,
  imports: [NgIf, MatIcon, NgForOf],
  templateUrl: './select-account-type.component.html',
  styleUrl: './select-account-type.component.scss',
})
export class SelectAccountTypeComponent {
  protected isOpened: boolean = false;
  protected typesSelect: AccountType[] = [];
  protected typesRows: number = 0;

  constructor(
    private selectAccountTypeServiceService: SelectAccountTypeServiceService,
    private createAccountService: CreateAccountService,
    private accountService: AccountService,
  ) {

    this.selectAccountTypeServiceService.modalOpened$.subscribe(
      (isOpened) => {
        this.accountService.getAccountTypes().subscribe((types) => {
          this.typesSelect = types;
          this.typesRows = this.typesSelect.length / 2;
        })

        this.showModal();
      },
    );
  }

  private showModal() {
    this.isOpened = true;
  }

  protected closeModal() {
    this.isOpened = false;
  }

  createAccountWithType(type: AccountType) {
    this.isOpened = false;
    this.createAccountService.openModal(type);
  }
}

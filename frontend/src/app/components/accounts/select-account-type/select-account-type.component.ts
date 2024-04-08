import { Component } from '@angular/core';
import { SelectAccountTypeServiceService } from '../../../services/communication/select-account-type-service.service';
import { NgForOf, NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { TypeSelect } from '../../../models/type-select';
import { CreateAccountService } from '../../../services/communication/create-account-service';

@Component({
  selector: 'app-select-account-type',
  standalone: true,
  imports: [NgIf, MatIcon, NgForOf],
  templateUrl: './select-account-type.component.html',
  styleUrl: './select-account-type.component.scss',
})
export class SelectAccountTypeComponent {
  protected isOpened: boolean = false;
  protected categoriesSelect: TypeSelect[] = [];
  protected categoriesRows: number = 0;

  constructor(
    private selectAccountTypeServiceService: SelectAccountTypeServiceService,
    private createAccountService: CreateAccountService,
  ) {
    this.categoriesSelect = [
      { id: 1, name: 'Bank accounts', iconName: 'card' },
      { id: 2, name: 'Cash', iconName: 'money-bag' },
      { id: 3, name: 'Stocks & Crypto', iconName: 'align' },
      { id: 4, name: 'Other assets', iconName: 'box' },
    ];
    this.categoriesRows = this.categoriesSelect.length / 2;

    this.selectAccountTypeServiceService.modalOpened$.subscribe(
      (isOpened) => {
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

  createAccountWithType(type: TypeSelect) {
    this.isOpened = false;
    this.createAccountService.openModal(type);
  }
}

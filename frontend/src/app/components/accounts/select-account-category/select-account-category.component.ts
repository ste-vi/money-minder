import { Component } from '@angular/core';
import { SelectAccountCategoryServiceService } from '../../../services/communication/select-account-category-service.service';
import { NgForOf, NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { CategorySelect } from '../../../models/category-select';
import { CreateAccountService } from '../../../services/communication/create-account-service';

@Component({
  selector: 'app-select-account-category',
  standalone: true,
  imports: [NgIf, MatIcon, NgForOf],
  templateUrl: './select-account-category.component.html',
  styleUrl: './select-account-category.component.scss',
})
export class SelectAccountCategoryComponent {
  protected isOpened: boolean = false;
  protected categoriesSelect: CategorySelect[] = [];
  protected categoriesRows: number = 0;

  constructor(
    private selectAccountCategoryServiceService: SelectAccountCategoryServiceService,
    private createAccountService: CreateAccountService,
  ) {
    this.categoriesSelect = [
      { id: 1, name: 'Bank accounts', iconName: 'card' },
      { id: 2, name: 'Cash', iconName: 'money-bag' },
      { id: 3, name: 'Stocks & Crypto', iconName: 'align' },
      { id: 4, name: 'Other assets', iconName: 'box' },
    ];
    this.categoriesRows = this.categoriesSelect.length / 2;

    this.selectAccountCategoryServiceService.modalOpened$.subscribe(
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

  createAccountWithCategory(category: CategorySelect) {
    this.isOpened = false;
    this.createAccountService.openModal(category);
  }
}

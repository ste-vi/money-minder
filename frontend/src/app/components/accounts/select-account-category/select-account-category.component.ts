import { Component } from '@angular/core';
import { SelectAccountCategoryServiceService } from '../../../services/select-account-category-service.service';
import { NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-select-account-category',
  standalone: true,
  imports: [NgIf, MatIcon],
  templateUrl: './select-account-category.component.html',
  styleUrl: './select-account-category.component.scss',
})
export class SelectAccountCategoryComponent {
  protected isOpened: boolean = false;

  constructor(
    private selectAccountCategoryServiceService: SelectAccountCategoryServiceService
  ) {
    this.selectAccountCategoryServiceService.modalOpened$.subscribe(
      (isOpened) => {
        this.showModal(isOpened);
      },
    );
  }

  private showModal(isOpened: boolean) {
    this.isOpened = isOpened;
  }

  protected closeModal() {
    this.isOpened = false;
  }
}

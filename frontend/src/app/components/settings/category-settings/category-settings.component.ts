import { Component } from '@angular/core';
import { CategorySettingsService } from '../../../services/communication/category-settings-service';
import { NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-category-settings',
  standalone: true,
  imports: [NgIf, MatIcon],
  templateUrl: './category-settings.component.html',
  styleUrl: './category-settings.component.scss',
})
export class CategorySettingsComponent {
  protected isOpened: boolean = true;

  constructor(private categorySettingsService: CategorySettingsService) {
    this.categorySettingsService.modalOpened$.subscribe((isOpened) => {
      this.showModal();
    });
  }

  private showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  addCategory() {}
}

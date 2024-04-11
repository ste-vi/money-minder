import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { CategorySettingsService } from '../../services/communication/category-settings-service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent {
  constructor(private categorySettingsService: CategorySettingsService) {}

  openCategorySettings() {
    this.categorySettingsService.openModal(true);
  }
}

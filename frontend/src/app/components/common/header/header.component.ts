import { Component, ElementRef } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { SelectAccountTypeServiceService } from '../../../services/communication/select-account-type-service.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatIcon, NgIf],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  protected space: string = 'Personal';
  protected showEdit: boolean = false;
  protected showAdd: boolean = false;
  protected showReload: boolean = false;
  protected showDots: boolean = false;

  constructor(
    private router: Router,
    private selectAccountTypeServiceService: SelectAccountTypeServiceService,
  ) {
    this.initButtons();
  }

  private initButtons() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const currentUrl = this.router.url;
        const path = currentUrl.split('/').pop();
        if (path == 'accounts') {
          this.showEdit = false;
          this.showAdd = true;
          this.showReload = true;
          this.showDots = true;
        } else if (path == 'dashboard') {
          this.showEdit = true;
          this.showAdd = false;
          this.showReload = true;
          this.showDots = false;
        } else if (path == 'settings') {
          this.showEdit = false;
          this.showAdd = false;
          this.showReload = false;
          this.showDots = true;
        }
      }
    });
  }

  openAddTypeModal() {
    this.selectAccountTypeServiceService.openModal(true);
  }
}

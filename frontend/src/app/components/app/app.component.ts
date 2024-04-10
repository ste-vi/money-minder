import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavComponent } from '../common/nav/nav.component';
import { HeaderComponent } from '../common/header/header.component';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { SelectAccountTypeComponent } from '../accounts/select-account-type/select-account-type.component';
import {CreateAccountComponent} from "../accounts/create-account/create-account.component";
import {TransactionViewComponent} from "../common/transaction/transaction-view/transaction-view.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavComponent,
    HeaderComponent,
    SelectAccountTypeComponent,
    CreateAccountComponent,
    TransactionViewComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'money-minder';

  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
  ) {
    this.initSvgIcons();
  }

  private initSvgIcons() {
    this.matIconRegistry.addSvgIcon(
      'dashboard',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/chart-square-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'wallet',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/wallet-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'settings',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/settings-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'reload',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/restart-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'edit',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/pen-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'spaces',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/layers-minimalistic-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'add',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/add-circle-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'dots',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/menu-dots-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'box',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/box-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'card',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/card-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'money-bag',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/money-bag-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'align',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/align-vertical-center-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'close',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/close-circle-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'calendar-search',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/calendar-search-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'wallet-2',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/wallet-2-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'delete',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/trash-bin-trash-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'arrow-left',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/arrow-left-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'textarea',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/sort-by-alphabet-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'alt-arrow-right',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/alt-arrow-right-svgrepo-com.svg',
      ),
    );
    this.matIconRegistry.addSvgIcon(
      'clipboard-check',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../assets/icons/svg/clipboard-check-svgrepo-com.svg',
      ),
    );
  }
}

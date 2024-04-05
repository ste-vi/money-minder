import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavComponent } from '../nav/nav.component';
import { HeaderComponent } from '../header/header.component';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavComponent, HeaderComponent],
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
  }
}

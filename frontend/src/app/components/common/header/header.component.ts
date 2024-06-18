import { Component, ElementRef } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { SelectAccountTypeServiceService } from '../../../services/communication/select-account-type-service.service';
import { SpaceService } from '../../../services/api/space-service';
import { Space } from '../../../models/space';
import { ProfileService } from '../../../services/communication/profile-service';
import {SpacesComponent} from "../spaces/spaces.component";
import {ViewSpacesService} from "../../../services/communication/view-spaces-service";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatIcon, NgIf, SpacesComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  protected space: Space | undefined = undefined;

  protected showEdit: boolean = false;
  protected showAdd: boolean = false;
  protected showReload: boolean = false;
  protected showUser: boolean = false;

  constructor(
    private router: Router,
    private selectAccountTypeServiceService: SelectAccountTypeServiceService,
    private spaceService: SpaceService,
    private profileService: ProfileService,
    private viewSpacesService: ViewSpacesService
  ) {
    this.initButtons();

    let localSpace = localStorage.getItem('space');
    if (localSpace === null) {
      this.spaceService.getCurrentSpace().subscribe((space) => {
        this.space = space;
        localStorage.setItem('space', JSON.stringify(space));
      });
    } else {
      this.space = JSON.parse(localSpace);
    }
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
          this.showUser = false;
        } else if (path == 'dashboard') {
          this.showEdit = true;
          this.showAdd = false;
          this.showReload = true;
          this.showUser = false;
        } else if (path == 'settings') {
          this.showEdit = false;
          this.showAdd = false;
          this.showReload = false;
          this.showUser = true;
        }
      }
    });
  }

  openAddTypeModal() {
    this.selectAccountTypeServiceService.openModal(true);
  }

  openProfile() {
    this.profileService.openModal(true);
  }

  openSpaces() {
    this.viewSpacesService.openModal()
  }
}

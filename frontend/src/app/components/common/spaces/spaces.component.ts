import { Component } from '@angular/core';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { ViewSpacesService } from '../../../services/communication/view-spaces-service';
import { Space } from '../../../models/space';
import { SpaceService } from '../../../services/api/space-service';
import { MatIcon } from '@angular/material/icon';
import { HttpResponse } from '@angular/common/http';
import { AuthService } from '../../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-spaces',
  standalone: true,
  imports: [NgIf, MatIcon, NgForOf, NgClass],
  templateUrl: './spaces.component.html',
  styleUrl: './spaces.component.scss',
})
export class SpacesComponent {
  protected isOpened: boolean = false;
  protected spaces: Space[] = [];
  protected currentSpace: Space | null = null;

  constructor(
    private viewSpacesService: ViewSpacesService,
    private spaceService: SpaceService,
    private authService: AuthService,
    private router: Router,
  ) {
    this.viewSpacesService.modalOpened$.subscribe(() => {
      this.isOpened = true;
      // todo: fix: it is called multiple times..
      this.spaceService
        .getSpaces()
        .subscribe((spaces) => (this.spaces = spaces));
    });

    // @ts-ignore
    this.currentSpace = JSON.parse(localStorage.getItem('space'));
  }

  closeModal() {
    this.isOpened = false;
  }

  switchToSpace(spaceId: string) {
    if (this.currentSpace?.id === spaceId) {
      return;
    }

    this.spaceService
      .switchSpace(spaceId)
      .subscribe((response: any) => {
        const headers = response.headers;
        console.log(headers);
        this.authService.setAccessToken(headers.get('Token')!);
        localStorage.setItem('space', JSON.stringify(response.body));
        window.location.reload();
      });
  }
}

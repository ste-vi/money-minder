import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ViewSpacesService {
  private openModalSource = new Subject<void>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal() {
    this.openModalSource.next();
  }
}

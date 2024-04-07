import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CategorySelect } from '../../models/category-select';

@Injectable({
  providedIn: 'root',
})
export class CreateAccountService {
  private openModalSource = new Subject<CategorySelect>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal(opened: CategorySelect) {
    this.openModalSource.next(opened);
  }
}

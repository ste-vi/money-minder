import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { TypeSelect } from '../../models/type-select';

@Injectable({
  providedIn: 'root',
})
export class ViewTransactionService {
  private openModalSource = new Subject<TypeSelect>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal(opened: TypeSelect) {
    this.openModalSource.next(opened);
  }
}

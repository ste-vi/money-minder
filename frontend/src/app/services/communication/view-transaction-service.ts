import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { TypeSelect } from '../../models/type-select';
import {Transaction} from "../../models/transaction";

@Injectable({
  providedIn: 'root',
})
export class ViewTransactionService {
  private openModalSource = new Subject<Transaction>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal(transaction: Transaction) {
    this.openModalSource.next(transaction);
  }
}

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AccountType } from '../../models/account-type';
import {Transaction} from "../../models/transaction";

@Injectable({
  providedIn: 'root',
})
export class SearchTransactionsService {
  private openModalSource = new Subject<boolean>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal() {
    this.openModalSource.next(true);
  }
}

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AccountType } from '../../models/account-type';
import {CategoryType} from "../../models/category";

@Injectable({
  providedIn: 'root',
})
export class SelectCategoryService {
  private openModalSource = new Subject<CategoryType>();

  modalOpened$ = this.openModalSource.asObservable();

  openModal(opened: CategoryType) {
    this.openModalSource.next(opened);
  }
}

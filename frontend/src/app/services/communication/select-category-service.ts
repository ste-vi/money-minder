import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {AccountType} from '../../models/account-type';
import {Category, CategoryType} from "../../models/category";

@Injectable({
  providedIn: 'root',
})
export class SelectCategoryService {
  private openModalSource = new Subject<CategoryType>();
  private categorySource = new Subject<Category>();

  modalOpened$ = this.openModalSource.asObservable();
  categorySelected$ = this.categorySource.asObservable();

  openModal(opened: CategoryType) {
    this.openModalSource.next(opened);
  }

  selectCategory(category: Category) {
    this.categorySource.next(category);
  }
}

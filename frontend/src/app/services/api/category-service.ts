import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Category, CategoryType } from '../../models/category';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  protected categories: Category[] = [];

  getCategories(): Observable<Category[]> {
    this.categories = [
      {
        id: 1,
        name: 'Transport',
        icon: 'box',
        position: 1,
        type: CategoryType.EXPENSE,
      },
      {
        id: 2,
        name: 'Bills',
        icon: 'box',
        position: 2,
        type: CategoryType.EXPENSE,
      },
      {
        id: 3,
        name: 'Home',
        icon: 'box',
        position: 3,
        type: CategoryType.EXPENSE,
      },
      {
        id: 4,
        name: 'Salary',
        icon: 'box',
        position: 1,
        type: CategoryType.INCOME,
      },
    ];
    return of(this.categories);
  }
}

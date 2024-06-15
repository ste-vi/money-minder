import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Category, CategoryType } from '../../models/category';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { TopExpense } from '../../models/top-expense';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private readonly rootUrl = environment.apiUrl + '/categories';

  constructor(private httpClient: HttpClient) {}

  getCategories(type?: CategoryType): Observable<Category[]> {
    let path = this.rootUrl;
    if (type) {
      path = path + '?type=' + type;
    }
    return this.httpClient.get<Category[]>(path);
  }

  getTopExpensesByCategories(
    dateFrom: Date,
    dateTo: Date,
  ): Observable<TopExpense[]> {
    let path = this.rootUrl + '/top-expenses?';
    path = path + '&dateFrom=' + dateFrom.toISOString().slice(0, -1);
    path = path + '&dateTo=' + dateTo.toISOString().slice(0, -1);
    return this.httpClient.get<TopExpense[]>(path);
  }
}

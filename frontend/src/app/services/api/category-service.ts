import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Category, CategoryType} from '../../models/category';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class CategoryService {

  private readonly rootUrl = environment.apiUrl + '/categories';

  constructor(private httpClient: HttpClient) {
  }

  getCategories(type?: CategoryType): Observable<Category[]> {
    let path = this.rootUrl;
    if (type) {
      path = path + '?type=' + type
    }
    return this.httpClient.get<Category[]>(path)
  }
}

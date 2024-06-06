import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Category} from '../../models/category';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class CategoryService {

  private readonly rootUrl = environment.apiUrl + '/categories';

  constructor(private httpClient: HttpClient) {
  }

  getCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(this.rootUrl)
  }
}

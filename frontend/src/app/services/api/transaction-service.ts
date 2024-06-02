import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Transaction} from '../../models/transaction';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {PageResponse} from "../../models/page-response";

@Injectable({
  providedIn: 'root',
})
export class TransactionService {

  readonly rootUrl = environment.apiUrl + '/transactions';

  constructor(private httpClient: HttpClient) {
  }

  getLastTransactions(size: number): Observable<PageResponse<Transaction>> {
    return this.httpClient.get<PageResponse<Transaction>>(this.rootUrl + '/search?size=' + size);
  }

  searchTransactions(page: number, size: number, searchQuery: string, accountId?: string): Observable<PageResponse<Transaction>> {
    let path = '/search?size=' + size + '&page=' + page;

    if (searchQuery) {
      path = path + '&name=' + searchQuery + '&notes=' + searchQuery;
    }
    if (accountId) {
      path = path + '&accountId=' + accountId;
    }

    return this.httpClient.get<PageResponse<Transaction>>(this.rootUrl + path);
  }

  update(id: string, updateRequest: { date: any; amount: any; notes: any; name: any; categoryId: string }) {
    return this.httpClient.put(this.rootUrl + '/' + id, updateRequest);
  }

  delete(id: string) {
    return this.httpClient.delete(this.rootUrl + '/' + id);
  }
}

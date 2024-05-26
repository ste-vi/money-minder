import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Transaction} from '../../models/transaction';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
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
}

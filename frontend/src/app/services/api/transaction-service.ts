import {Injectable} from '@angular/core';
import {Observable, Subject, tap} from 'rxjs';
import {Transaction, TransactionType} from '../../models/transaction';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {PageResponse} from "../../models/page-response";
import {Currency} from "../../models/currency";

@Injectable({
  providedIn: 'root',
})
export class TransactionService {

  readonly rootUrl = environment.apiUrl + '/transactions';

  private refreshTransactionsSubject = new Subject<number>();

  constructor(private httpClient: HttpClient) {
  }

  getLastTransactions(size: number): Observable<PageResponse<Transaction>> {
    return this.httpClient.get<PageResponse<Transaction>>(this.rootUrl + '/search?size=' + size);
  }

  searchTransactions(page: number,
                     size: number,
                     searchQuery: string,
                     accountId?: string,
                     needReview?: boolean,
                     dateFrom?: Date,
                     dateTo?: Date): Observable<PageResponse<Transaction>> {
    let path = '/search?size=' + size + '&page=' + page;

    if (searchQuery) {
      path = path + '&name=' + searchQuery + '&notes=' + searchQuery;
    }
    if (accountId) {
      path = path + '&accountId=' + accountId;
    }
    if (needReview) {
      path = path + '&needReview=' + needReview;
    }
    if (dateFrom) {
      path = path + '&dateFrom=' + dateFrom.toISOString().slice(0, -1);
    }
    if (dateTo) {
      path = path + '&dateTo=' + dateTo.toISOString().slice(0, -1);
    }

    return this.httpClient.get<PageResponse<Transaction>>(this.rootUrl + path);
  }

  create(createRequest: {
    fromAccountId: string,
    currency: Currency,
    date: Date;
    amount: number;
    notes: string;
    name: string;
    categoryId?: string,
    type: TransactionType
  }): Observable<Transaction> {
    return this.httpClient.post<Transaction>(this.rootUrl, createRequest)
      .pipe(tap(() => {
          this.refreshTransactionsSubject.next(createRequest.amount);
        })
      );
  }

  update(id: string, updateRequest: { date: any; amount: any; notes: any; name: any; categoryId?: string }) {
    return this.httpClient.put(this.rootUrl + '/' + id, updateRequest);
  }

  delete(transaction: Transaction) {
    return this.httpClient.delete(this.rootUrl + '/' + transaction.id).pipe(tap(() => {
        this.refreshTransactionsSubject.next(transaction.amount);
      })
    );
  }

  get refreshTransactions$(): Observable<number> {
    return this.refreshTransactionsSubject.asObservable();
  }
}

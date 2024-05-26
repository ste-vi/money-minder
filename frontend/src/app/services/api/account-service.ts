import {Injectable} from '@angular/core';
import {Observable, Subject, tap} from 'rxjs';
import {Type} from '../../models/type';
import {Account} from '../../models/account';
import {HttpClient} from '@angular/common/http';
import {environment} from "../../../environments/environment";
import {AccountType} from "../../models/account-type";

@Injectable({
  providedIn: 'root',
})
export class AccountService {

  readonly rootUrl = environment.apiUrl + '/accounts';

  private newAccountSubject = new Subject<void>();

  constructor(private httpClient: HttpClient) {
  }

  getAccounts(): Observable<Account[]> {
    return this.httpClient.get<Account[]>(this.rootUrl)
  }

  getAccountTypes(): Observable<AccountType[]> {
    return this.httpClient.get<AccountType[]>(this.rootUrl + "/types")
  }

  createAccount(account: any): Observable<any> {
    return this.httpClient.post(this.rootUrl, account)
      .pipe(tap(() => {
          this.newAccountSubject.next();
        })
      );
  }

  get newAccount$(): Observable<void> {
    return this.newAccountSubject.asObservable();
  }
}

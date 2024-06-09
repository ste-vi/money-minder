import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Rule} from "../../models/rule";

@Injectable({
  providedIn: 'root',
})
export class RuleService {

  readonly rootUrl = environment.apiUrl + '/rules';

  constructor(private httpClient: HttpClient) {
  }

  getRules(): Observable<Rule[]> {
    return this.httpClient.get<Rule[]>(this.rootUrl);
  }
}

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Space} from "../../models/space";

@Injectable({
  providedIn: 'root',
})
export class SpaceService {

  private readonly rootUrl = environment.apiUrl + '/spaces';

  constructor(private httpClient: HttpClient) {
  }

  getCurrentSpace(): Observable<Space> {
    return this.httpClient.get<Space>(this.rootUrl)
  }
}

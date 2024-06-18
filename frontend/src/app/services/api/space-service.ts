import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import { Space } from '../../models/space';

@Injectable({
  providedIn: 'root',
})
export class SpaceService {
  private readonly rootUrl = environment.apiUrl + '/spaces';

  constructor(private httpClient: HttpClient) {}

  getCurrentSpace(): Observable<Space> {
    return this.httpClient.get<Space>(this.rootUrl + '/current');
  }

  getSpaces(): Observable<Space[]> {
    return this.httpClient.get<Space[]>(this.rootUrl);
  }

  switchSpace(id: string): Observable<any> {
    return this.httpClient.get<Space>(this.rootUrl + '/switch/' + id, { observe: 'response' });
  }
}

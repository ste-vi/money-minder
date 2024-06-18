import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false,
  );

  constructor(
    private router: Router,
    private jwtHelper: JwtHelperService,
  ) {}

  get isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('accessToken');
    if (token !== 'null' && token !== undefined) {
      this.loggedIn.next(!this.jwtHelper.isTokenExpired(token));
      return !this.jwtHelper.isTokenExpired(token);
    } else {
      this.loggedIn.next(false);
      return false;
    }
  }

  loginViaGoogle(): void {
    window.open(environment.apiUrl + '/login', '_self');
  }

  setAccessToken(accessToken: string): void {
    localStorage.setItem('accessToken', accessToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  logout(): void {
    this.removeAuthorization();
    this.router.navigate(['/auth']).then((r) => '');
  }

  removeAuthorization(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('space');
  }
}

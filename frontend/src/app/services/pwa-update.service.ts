
import { Injectable } from '@angular/core';
import { SwUpdate, VersionReadyEvent } from '@angular/service-worker';
import { filter } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PwaUpdateService {

  constructor(private swUpdate: SwUpdate) {
    if (swUpdate.isEnabled) {
      setInterval(() => {
        swUpdate.checkForUpdate();
      }, 6 * 60 * 60 * 1000);

      swUpdate.versionUpdates
        .pipe(filter((evt): evt is VersionReadyEvent => evt.type === 'VERSION_READY'))
        .subscribe(evt => {
          if (this.promptUser()) {
            window.location.reload();
          }
        });
    }
  }

  private promptUser(): boolean {
    return confirm('New version available. Load New Version?');
  }

  checkForUpdate(): void {
    if (this.swUpdate.isEnabled) {
      this.swUpdate.checkForUpdate();
    }
  }
}

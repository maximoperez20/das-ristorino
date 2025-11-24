import { ErrorHandler, Injectable, inject, isDevMode, NgZone } from '@angular/core';
import { IMessage } from '../models/i-message';
import { AppMessageService } from '../services/app-message-service';

@Injectable()
export class AppErrorHandler implements ErrorHandler {

  private readonly _service = inject(AppMessageService);
  private readonly _zone = inject(NgZone);

  handleError(error: any): void {
    let message: IMessage;

    if (error.rejection) {
      error = error.rejection;
    }

    if (error.body) {
      if (error.body.message) {
        message = { text: error.body.message, num: error.status };
      }
      else if (error.body.error) {
        message = { text: error.body.error, num: error.status };
      }
      else {
        if (error.status == 0) {
          message = { text: $localize`Error al conectarse al servicio`, num: error.status };
        }
        else {
          message = { text: error.body, num: error.status };
        }
      }
    }
    else if (error.error) {
      message = { text: error.error, num: error.status };
    }
    else if (error.message) {
      message = { text: error.message, num: error.status };
    }
    else {
      message = { text: error, num: error.status };
    }

    if (isDevMode()) {
      console.error('[ErrorHandler]', error);
    }

    this._zone.run(() => this._service.showMessage(message));
  }

}

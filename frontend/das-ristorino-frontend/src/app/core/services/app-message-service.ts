import { inject, Injectable } from '@angular/core';
import { IMessage } from '../models/i-message';
import { MatDialog } from '@angular/material/dialog';
import { MessageDialog } from '../layouts/message-dialog/message-dialog';

@Injectable()
export class AppMessageService {
  
  private dialog = inject(MatDialog);

  showMessage(message: IMessage): void {
    this.dialog.open(MessageDialog, {
      data: message,
      width: '380px',
      disableClose: true,
      autoFocus: false,
    });
  }

}

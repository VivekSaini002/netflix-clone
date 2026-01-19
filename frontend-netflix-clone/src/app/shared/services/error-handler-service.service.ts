import { Injectable } from '@angular/core';
import { NotificationServiceService } from './notification-service.service';

@Injectable({
  providedIn: 'root',
})
export class ErrorHandlerServiceService {
  constructor(private notification: NotificationServiceService) {}

  handle(err: any, fallbackMessage: string) {
    const errorMsg = err.error?.error || fallbackMessage;
    this.notification.error(errorMsg);
    console.error('API Error: ', err);
  }
}
